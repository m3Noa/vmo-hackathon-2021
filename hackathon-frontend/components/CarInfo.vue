<template>
  <v-card class="mx-auto" max-width="600">
    <v-img
      class="white--text align-end"
      aspect-ratio="1.75"
      :src="displayItem.imageUrl"
    >
      <v-card-title class="text-shadow">
        <div>
          {{ displayItem.name }}
          <v-card-subtitle class="white--text pa-0 align-content-start">
            Type: {{ displayItem.category.name }}
          </v-card-subtitle>
        </div>
      </v-card-title>
    </v-img>

    <v-card-subtitle class="pb-0 align-content-start">
      Description
    </v-card-subtitle>

    <v-card-text class="text--primary mx-auto row">
      <div class="col-12 elevation-2 mb-4">
        {{ displayItem.description }}
      </div>

      <div class="col-6 pa-0">
        State: {{ displayItem.isActive ? 'Active' : 'Disabled' }}
      </div>
      <div class="col-6 pa-0">
        Rental Status: {{ displayItem.rentalStatus ? 'Rented' : 'Available' }}
      </div>
      <div class="col-6 pa-0">
        In Service Since: {{ displayItem.createAt.split(' ')[0] }}
      </div>
      <div class="col-6 pa-0">
        Last Rented: {{ displayItem.updateAt.split(' ')[0] }}
      </div>
    </v-card-text>

    <v-card-actions>
      <v-btn color="orange" text> Share </v-btn>
      <v-spacer></v-spacer>
      <v-dialog
        v-model="dialog"
        transition="dialog-top-transition"
        max-width="400"
      >
        <template #activator="{ on, attrs }">
          <v-btn
            small
            text
            color="orange"
            v-bind="attrs"
            v-on="on"
            @click="booking"
          >
            New Booking
          </v-btn>
        </template>
        <booking @onSubmitBooking="dialog = false" />
      </v-dialog>
    </v-card-actions>

    <time-table :cars="[displayItem.id]" />
  </v-card>
</template>
<script>
import { mapState, mapMutations } from 'vuex'
import TimeTable from '@/components/TimeTable'
import Booking from '@/components/Booking'

export default {
  components: { TimeTable, Booking },
  props: {
    item: {
      required: false,
      type: Array,
      default: null,
    },
  },

  data: () => ({
    status: 'Succeeded',
    dialog: false,
  }),

  computed: {
    ...mapState({
      selectedItem: 'selectedItem',
    }),
    displayItem() {
      // console.log({ input: this.item, cached: this.setSelectedItem })
      return this.item || this.selectedItem
    },
  },

  methods: {
    ...mapMutations({
      setSelectedItem: 'setSelectedItem',
    }),
    booking() {
      // console.log
    },
  },
}
</script>
<style lang="scss">
.text-shadow {
  text-shadow: 0px 4px 4px #333f;
}
</style>
