<template>
  <v-row justify="center" cols="12" class="fluid col-12">
    <v-card-text>
      <Breadcrumbs :items="breadcrumbs"></Breadcrumbs>
    </v-card-text>
    <v-col cols="12">
      <v-card>
        <v-card-title>Your TimeTable</v-card-title>
        <v-divider />
        <v-card-text> <time-table :cars="carInRentals" /> </v-card-text>
      </v-card>
    </v-col>
    <v-col cols="12">
      <v-card>
        <data-table
          :title="titles.cars"
          :source="cars"
          :selection-sources="[
            { key: 'categoryId', data: minification(categoryList) },
          ]"
          :disables="['categoryCode']"
          :api-url="api.car"
          @dataUpdated="setItemList"
        />
      </v-card>
    </v-col>
    <v-col cols="12">
      <v-card>
        <data-table
          :title="titles.rentals"
          :source="rentals"
          :selection-sources="[
            {
              key: 'customerId',
              data: minification(customerList, 'id', ['firstName', 'lastName']),
            },
            { key: 'carId', data: minification(cars) },
          ]"
          :disables="['car', 'customer']"
          :api-url="api.rental"
          api-optional-ept="cancel"
          @dataUpdated="setRentalList"
        />
      </v-card>
    </v-col>
  </v-row>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import api from '@/config/api'
import DataTable from '@/components/DataTable'
import TimeTable from '@/components/TimeTable.vue'
import Breadcrumbs from '@/components/Breadcrumbs'

export default {
  components: {
    DataTable,
    TimeTable,
    Breadcrumbs,
  },
  middleware({ store, redirect }) {
    // If the user is not authenticated or data is not populated yet
    if (!store.state.isAdmin) {
      return redirect('/rental-cars')
    }
  },
  data: () => ({
    breadcrumbs: [
      {
        text: 'VMO Cars',
      },
      {
        text: 'Dashboard',
      },
    ],
    carInRentals: [],
    titles: {
      cars: 'Your Cars',
      rentals: 'Your Rental Bookings',
    },
    api,
  }),
  async fetch() {
    const syncStatus = await fetch(api.status).then((result) => result.text())
    // // // console.log({ status: syncStatus })
    if (syncStatus !== this.dataStatus) {
      await this.$store.dispatch('updateData', syncStatus)
    }
  },
  computed: {
    ...mapState([
      'dataStatus',
      'itemList',
      'rentalList',
      'categoryList',
      'customerList',
      'userItemList',
      'userRentalList',
    ]),
    cars() {
      const cars = []
      for (const car of this.itemList) {
        if (this.userItemList.includes(car.id)) {
          cars.push(car)
        }
      }

      return cars
    },
    rentals() {
      const rentals = []
      for (const rental of this.rentalList) {
        if (this.userRentalList.includes(rental.id)) {
          rentals.push(rental)
          this.setCarId(rental.carId)
        }
      }

      return rentals
    },
  },
  methods: {
    ...mapMutations(['setItemList', 'setRentalList']),
    minification(source, value = 'id', names = ['name']) {
      const min = []
      let label = ''
      for (const item of source) {
        const texts = []
        for (const name of names) {
          texts.push(item[name])
        }
        label = texts.join(' ')
        min.push({ value: item[value], text: label })
      }
      // // console.log({ source, min })
      return min
    },
    setCarId(id) {
      this.carInRentals.push(id)
    },
  },
}
</script>
