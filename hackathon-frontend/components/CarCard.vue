<template>
  <v-row class="v-card elevation-2 ma-2 rounded car_info">
    <v-col cols="12" sm="6">
      <v-img :src="car.imageUrl" aspect-ratio="1.6" alt="Car Image" />
    </v-col>
    <v-col cols="12" sm="6" class="specifications">
      <h3>{{ car.name }}</h3>
      <div>
        <p>
          <v-icon>mdi-list-status</v-icon>
          <span>Status:</span>
          <v-chip v-if="car.rentalStatus" color="red" outlined small>
            Rented
          </v-chip>
          <v-chip v-else color="green" outlined small> Available </v-chip>
        </p>
        <div>
          <v-icon>mdi-book-open-outline</v-icon>
          <span>Description:</span>
          <span class="quote line-clamp">{{ car.description }}</span>
        </div>
      </div>
    </v-col>
    <v-col cols="12" class="pt-0">
      <v-btn plain small class="car-tag">
        <v-icon>mdi-tag</v-icon>
        <i class="optional">Category:</i>&nbsp;&nbsp;{{ categoryName }}
      </v-btn>
      <v-btn plain small class="car-tag">
        <v-icon>mdi-currency-usd</v-icon>
        <i class="optional">Price:</i>&nbsp;&nbsp;
        <v-chip x-small color="#cc7400" outlined label>
          {{ car.pricePerDay }}$/day
        </v-chip>
      </v-btn>
      <!-- <NuxtLink :to="`/rent-cars/${car.id}`"> -->
      <v-dialog
        v-model="dialog"
        transition="dialog-top-transition"
        max-width="600"
      >
        <template #activator="{ on, attrs }">
          <v-btn
            small
            class="view_btn col-4"
            color="#ff9800"
            v-bind="attrs"
            v-on="on"
            @click="getCarInfo"
          >
            View detail&nbsp;
            <v-icon>mdi-chevron-right</v-icon>
          </v-btn>
        </template>
        <car-info />
      </v-dialog>
      <!-- </NuxtLink> -->
    </v-col>
  </v-row>
</template>

<script>
import CarInfo from '@/components/CarInfo'

export default {
  components: { CarInfo },
  props: {
    car: {
      required: true,
      type: Object,
    },
    categoryName: {
      required: true,
      type: String,
    },
  },

  data: () => ({
    dialog: false,
  }),

  methods: {
    getCarInfo() {
      this.$store.dispatch('getSelectedItem', this.car.id)
    },
  },
}
</script>

<style lang="scss" scoped>
$chip-padding: 0 !default;
.car_info {
  margin-left: 20px;
  font-size: 85%;
  .car-tag {
    font-size: 70%;
    padding-left: 0;
    margin-left: 0;
  }
  .specifications {
    margin-top: 5px;
    font-weight: inherit;
    vertical-align: middle;
    .quote {
      font-size: 85%;
      border-left: solid #ccc 2px;
      padding: 0 5px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
  @media only screen and (max-width: 508px) and (min-width: 399px) {
    .optional {
      display: none;
    }
  }
  @media only screen and (max-width: 1016px) and (min-width: 798px) {
    .optional {
      display: none;
    }
  }
  .view_btn {
    float: right;
    min-width: 132px;
  }
}
</style>
