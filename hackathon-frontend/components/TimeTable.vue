<template>
  <div>
    <v-sheet tile height="54" class="d-flex">
      <v-btn icon class="ma-2" @click="$refs.calendar.prev()">
        <v-icon>mdi-chevron-left</v-icon>
      </v-btn>
      <v-spacer></v-spacer>
      <v-btn icon class="ma-2" @click="$refs.calendar.next()">
        <v-icon>mdi-chevron-right</v-icon>
      </v-btn>
    </v-sheet>
    <v-sheet height="auto">
      <v-calendar
        ref="calendar"
        v-model="value"
        short-weekdays
        :start="timeRange.dateStart"
        :end="timeRange.dateEnd"
        :min-weeks="2"
        :weekdays="weekday"
        :type="type"
        :events="rentals"
        :event-overlap-mode="mode"
        :event-overlap-threshold="30"
        :event-color="getRentalColor"
        @change="getRentals"
      >
      </v-calendar>
    </v-sheet>
  </div>
</template>
<script>
import { mapState } from 'vuex'

const today = new Date()
const nextWeek = Date(
  today.getFullYear(),
  today.getMonth(),
  today.getDate() + 7
)

export default {
  props: {
    cars: {
      type: Array,
      default: null,
    },
    timeRange: {
      type: Object,
      default: () => ({
        dateStart: today,
        dateEnd: nextWeek,
      }),
    },
  },
  data: () => ({
    type: 'week', // 'custom-weekly',
    types: ['month', 'week', 'day', '4day'],
    mode: 'stack',
    modes: ['stack', 'column'],
    weekday: [1, 2, 3, 4, 5, 6, 0],
    value: '',
    rentals: [],
  }),
  computed: {
    ...mapState(['itemList', 'rentalList']),
    carName() {
      const names = {}
      for (const car of this.itemList) {
        names[car.id] = car.name
      }
      return names
    },
  },
  methods: {
    getRentals() {
      const rentals = []
      const userRentals = []

      for (const rental of this.rentalList) {
        const allDay = true
        if (this.cars !== null) {
          if (this.cars.includes(rental.carId)) {
            userRentals.push({
              name: this.carName[rental.carId],
              start: rental.fromDate.split(' ')[0],
              end: rental.dueDate.split(' ')[0],
              timed: !allDay,
            })
          }
        }
        rentals.push({
          name: this.carName[rental.carId],
          start: rental.fromDate.split(' ')[0],
          end: rental.dueDate.split(' ')[0],
          timed: !allDay,
        })
      }

      this.rentals = this.cars !== null ? userRentals : rentals
    },
    getRentalColor(rental) {
      const str = rental.name
      let hash = 0
      for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 3) - hash)
      }
      const color = Math.abs(hash).toString(16).substring(0, 6)
      return '#' + '000000'.substring(0, 6 - color.length) + color
    },
    rnd(a, b) {
      return Math.floor((b - a + 1) * Math.random()) + a
    },
  },
}
</script>
<style lang="scss">
.v-calendar-daily__body,
.v-calendar-daily__intervals-head {
  display: none;
}
</style>
