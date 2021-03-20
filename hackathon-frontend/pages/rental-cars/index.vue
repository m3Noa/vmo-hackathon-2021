<template>
  <div>
    <v-card-text>
      <Breadcrumbs :items="breadcrumbs"></Breadcrumbs>
    </v-card-text>

    <div v-if="$fetchState.error">
      <v-alert outlined type="error">Failed to load data</v-alert>
    </div>
    <div v-if="!$fetchState.pending && !$fetchState.error" class="page_content">
      <v-card class="px-4">
        <v-row class="search_bar" justify="center">
          <v-col lg="4" cols="12">
            <v-text-field
              v-model="filter.searchText"
              label="Search by name"
              hide-details
              dense
              outlined
            ></v-text-field>
          </v-col>
          <v-col lg="2" cols="6">
            <v-select
              v-model="filter.status"
              :items="status"
              label="Status"
              hide-details
              dense
              outlined
            ></v-select>
          </v-col>
          <v-col lg="2" cols="6">
            <v-select
              v-model="filter.categoryId"
              :items="categorySelectItems"
              label="Category"
              hide-details
              dense
              outlined
            ></v-select>
          </v-col>
          <v-col lg="2" cols="12">
            <v-menu
              ref="menu1"
              :close-on-content-click="false"
              transition="scale-transition"
              offset-y
              max-width="290px"
              min-width="auto"
            >
              <template #activator="{ on, attrs }">
                <v-text-field
                  v-model="dateRangeText"
                  label="Start date"
                  persistent-hint
                  prepend-icon="mdi-calendar"
                  v-bind="attrs"
                  dense
                  hide-details
                  outlined
                  readonly
                  v-on="on"
                ></v-text-field>
              </template>
              <v-date-picker
                v-model="filter.rangeDate"
                no-title
                range
                @input="menu1 = false"
              ></v-date-picker>
            </v-menu>
          </v-col>
          <v-col lg="1" cols="6" align="center">
            <v-btn class="primary" @click="search">Search</v-btn>
          </v-col>
          <v-col lg="1" cols="6" align="center">
            <v-btn color="#D8D8D8" @click="resetFilter">Reset</v-btn>
          </v-col>
        </v-row>
      </v-card>

      <v-alert v-if="!carListDisplay.length" outlined type="error">
        No result found
      </v-alert>
      <v-row dense>
        <v-col
          v-for="car in carListDisplay"
          :key="car.id"
          class="car_card"
          cols="12"
          xl="4"
          md="6"
          sm="12"
        >
          <car-card
            :car="car"
            :category-name="
              categories[car.categoryId]
                ? categories[car.categoryId]
                : 'Undefined'
            "
          >
          </car-card>
        </v-col>
      </v-row>
    </div>
  </div>
</template>

<script>
import Breadcrumbs from '@/components/Breadcrumbs'
import api from '@/config/api.js'
import { mapState, mapMutations } from 'vuex'
import CarCard from '~/components/CarCard'

export default {
  components: {
    Breadcrumbs,
    CarCard,
  },
  data: () => {
    return {
      breadcrumbs: [
        {
          text: 'VMO Cars',
        },
        {
          text: 'Car Listing',
        },
      ],
      status: [
        {
          text: 'Avaiable',
          value: 0,
        },
        {
          text: 'Rented',
          value: 1,
        },
      ],
      categories: {},
      categorySelectItems: [],
      carListDisplay: [],
      filter: {
        searchText: '',
        status: null,
        categoryId: null,
        rangeDate: [],
      },
    }
  },
  async fetch() {
    const syncStatus = await fetch(api.status).then((result) => result.text())
    if (syncStatus !== this.dataStatus) {
      await this.$store.dispatch('updateData', syncStatus)

      this.categoryList.forEach(function (category) {
        this[category.id] = category.name
      }, this.categories)
      this.categoryList.forEach(function (category) {
        this.push({
          text: category.name,
          value: category.id,
        })
      }, this.categorySelectItems)
    }

    this.carListDisplay = Object.assign([], this.carList)
  },

  computed: {
    // dataStatus() {
    //   return this.$store.state.dataStatus
    // },
    ...mapState({
      dataStatus: 'dataStatus',
      categoryList: 'categoryList',
      carList: 'itemList',
      rangeDate: 'rangeDate',
    }),
    dateRangeText() {
      return this.filter.rangeDate.join(' ~ ')
    },
  },

  watch: {
    dateRangeText() {
      this.setRangeDate(this.filter.rangeDate)
    },
  },

  methods: {
    ...mapMutations([
      'setItemList',
      'setStatus',
      'setCategoryList',
      'setRangeDate',
    ]),
    search() {
      if (
        !this.filter.searchText &&
        this.filter.status === null &&
        !this.filter.categoryId
      ) {
        this.carListDisplay = Object.assign({}, this.carList)
        return
      }
      const result = []
      this.carList.forEach((car) => {
        if (this.filter.searchText) {
          if (
            !car.name
              .toLowerCase()
              .includes(this.filter.searchText.toLowerCase())
          ) {
            return 0
          }
        }
        if (this.filter.status !== null) {
          if (car.rentalStatus !== this.filter.status) {
            return 0
          }
        }
        if (this.filter.categoryId) {
          if (car.categoryId !== this.filter.categoryId) {
            return 0
          }
        }
        result.push(car)
      })
      this.carListDisplay = Object.assign([], result)
    },
    resetFilter() {
      this.carListDisplay = Object.assign([], this.carList)
      this.filter = {
        searchText: '',
        status: null,
        categoryId: null,
        rangeDate: [],
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.page_content {
  .search_bar {
    margin-top: 10px;
    margin-bottom: 20px;
    padding: 5px;
  }
}
// .car_card {
//   padding-right: 10px;
//   margin-bottom: 10px;
// }
</style>
