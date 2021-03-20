<template>
  <v-card>
    <v-card-title class="orange lighten-1">
      New Booking: {{ selectedItem.name }}
    </v-card-title>
    <v-divider />
    <v-card-text>
      <v-col cols="12">
        <v-menu
          ref="bookingTime"
          :close-on-content-click="false"
          transition="scale-transition"
          offset-y
          max-width="290px"
          min-width="auto"
        >
          <template #activator="{ on, attrs }">
            <v-text-field
              v-model="dateRangeText"
              label="Booking Days"
              persistent-hint
              prepend-icon="mdi-calendar"
              v-bind="attrs"
              dense
              outlined
              hide-details
              v-on="on"
            ></v-text-field>
          </template>
          <v-date-picker
            v-model="submitData.rangeDate"
            no-title
            range
            :min="Date.now().toString()"
            @input="bookingTime = false"
          ></v-date-picker>
        </v-menu>
      </v-col>
      <v-col>
        <v-container fluid>
          <v-row>
            <v-col cols="6 ma-0 pa-1">
              <v-subheader>Days to Rent</v-subheader>
            </v-col>
            <v-col cols="6 ma-0 pa-1">
              <v-text-field
                disabled
                dense
                hide-details
                :value="getDays"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col cols="6 ma-0 pa-1">
              <v-subheader>Discount</v-subheader>
            </v-col>
            <v-col cols="6 ma-0 pa-1">
              <v-text-field
                disabled
                dense
                hide-details
                :value="0"
                suffix="%"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col cols="6 ma-0 pa-1">
              <v-subheader>Tax</v-subheader>
            </v-col>
            <v-col cols="6 ma-0 pa-1">
              <v-text-field
                disabled
                dense
                hide-details
                :value="0"
                suffix="%"
              ></v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col cols="6 ma-0 pa-1">
              <v-subheader>Total Price</v-subheader>
            </v-col>
            <v-col cols="6 ma-0 pa-1">
              <v-text-field
                disabled
                dense
                hide-details
                :value="totalPrice"
                prefix="$"
                suffix="USD"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-container>
      </v-col>
    </v-card-text>
    {{ msg }}
    <v-card-actions>
      <v-btn block @click="submitBooking">Submit</v-btn>
    </v-card-actions>
  </v-card>
</template>
<script>
import { mapState, mapMutations } from 'vuex'
import api from '@/config/api'

export default {
  props: {
    data: {
      type: Object,
      default: null,
    },
  },
  data: () => ({
    submitData: {
      carId: '',
      customerId: '',
      rangeDate: [],
      fromDate: '',
      dueDate: '',
      createBy: '',
    },
    msg: '',
    invalid: false,
  }),

  computed: {
    ...mapState({
      rangeDate: 'rangeDate',
      selectedItem: 'selectedItem',
      user: 'user',
    }),
    dateRangeText() {
      return this.submitData.rangeDate.join(' ~ ')
    },
    getDays() {
      this.getDate()
      const dateNow = Date.now()
      const date0 = new Date(this.submitData.fromDate)
      const date1 = new Date(this.submitData.dueDate)

      this.checkDates(date0, dateNow)

      return (date1.getTime() - date0.getTime()) / (1000 * 3600 * 24) + 1
    },
    totalPrice() {
      return this.selectedItem.pricePerDay * this.getDays
    },
  },

  watch: {
    dateRangeText() {
      this.setRangeDate(this.submitData.rangeDate)
    },
  },

  mounted() {
    if (this.rangeDate !== []) this.submitData.rangeDate = [...this.rangeDate]
  },

  methods: {
    ...mapMutations({
      setRangeDate: 'setRangeDate',
    }),
    getDate() {
      this.submitData.rangeDate.sort()
      this.submitData.fromDate = this.submitData.rangeDate[0]
      this.submitData.dueDate = this.submitData.rangeDate[1]
      // // console.log(this.fromDate, this.dueDate)
    },
    checkDates(dateSelect, dateNow) {
      if (dateNow > dateSelect) {
        this.msg =
          'The chosen date range is invalid! Cannot select day ealier than today.'
        this.invalid = true
      } else {
        this.msg = ''
        this.invalid = false
      }
    },

    async submitBooking() {
      if (!this.invalid) {
        this.submitData.carId = this.selectedItem.id
        this.submitData.customerId = this.user.id
        this.submitData.createBy = this.user.id
        this.submitData.fromDate += ' 06:00:00+0700'
        this.submitData.dueDate += ' 06:00:00+0700'
        await this.$axios.$post(api.rental, this.submitData).then((result) => {
          if (result.status !== 'Succeeded') alert(result.message)
          else {
            // console.log({ result })
          }
        })
        this.$emit('onSubmitBooking')
      }
    },
  },
}
</script>
