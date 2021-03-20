<template>
  <div>
    <v-row>
      <v-col>
        <v-card>
          <data-table
            :title="titles.customers"
            :source="customers"
            :disables="[]"
            :api-url="api.customer"
            api-optional-ept="deactive"
            @dataUpdated="setCustomerList"
          />
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <data-table
            :title="titles.categories"
            :source="categories"
            :disables="[]"
            :api-url="api.category"
            @dataUpdated="setCategoryList"
          />
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <data-table
            :title="titles.cars"
            :source="cars"
            :selection-sources="[
              { key: 'categoryId', data: minification(categories) },
            ]"
            :disables="['categoryCode']"
            :api-url="api.car"
            @dataUpdated="setItemList"
          />
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <data-table
            :title="titles.rentals"
            :source="rentals"
            :selection-sources="[
              {
                key: 'customerId',
                data: minification(customers, 'id', ['firstName', 'lastName']),
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
  </div>
</template>
<script>
import { mapState, mapMutations } from 'vuex'
import api from '@/config/api'
import DataTable from '@/components/DataTable'

export default {
  components: {
    DataTable,
  },
  middleware({ store, redirect }) {
    // If the user is not authenticated or data is not populated yet
    if (!store.state.isAdmin) {
      return redirect('/rental-cars')
    }
  },
  data: () => ({
    editing: null,
    titles: {
      categories: 'Car Category List [CRUD]',
      cars: 'Car List [CRUD]',
      customers: 'Customer List [CRUD]',
      rentals: 'Rental List [CRUD]',
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
    ...mapState({
      isAdmin: 'isAdmin',
      dataStatus: 'dataStatus',
      categories: 'categoryList',
      cars: 'itemList',
      customers: 'customerList',
      rentals: 'rentalList',
    }),
  },
  methods: {
    ...mapMutations([
      'setItemList',
      'setCategoryList',
      'setCustomerList',
      'setRentalList',
    ]),
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
  },
}
</script>
