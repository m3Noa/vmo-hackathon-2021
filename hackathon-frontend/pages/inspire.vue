<template>
  <v-row>
    <v-col class="text-center">
      <car-info />
    </v-col>
  </v-row>
</template>

<script>
import CarInfo from '../components/CarInfo.vue'
export default {
  components: {
    CarInfo,
  },
}
</script>
