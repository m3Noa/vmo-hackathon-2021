<template>
  <v-row>
    <v-col>
      <div>
        <v-breadcrumbs :items="items" large style="padding: 0"></v-breadcrumbs>
      </div>
    </v-col>
  </v-row>
</template>

<script>
export default {
  props: {
    items: {
      type: Array,
      required: true,
      default: null,
    },
  },
}
</script>
