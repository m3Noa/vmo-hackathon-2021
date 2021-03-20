<template>
  <v-data-table
    :headers="headers"
    :items="tableData"
    sort-by="id"
    class="elevation-1 col-12 vmo-d-table"
    dense
    no-data-text="The List is empty"
  >
    <template #top>
      <v-toolbar flat>
        <v-toolbar-title>{{ title }}</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-divider class="mx-4" inset vertical></v-divider>
        <v-dialog v-model="dialog" max-width="800px">
          <template #activator="{ on, attrs }">
            <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
              New +
            </v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row>
                  <template v-for="field in headers">
                    <v-col
                      v-if="field.value !== 'id'"
                      :key="field.value"
                      cols="12"
                      sm="6"
                    >
                      <v-text-field
                        v-if="
                          field.value !== 'actions' && field.type !== 'select'
                        "
                        v-model="editedItem[field.value]"
                        :label="field.text"
                      ></v-text-field>
                      <v-select
                        v-if="field.type === 'select'"
                        v-model="editedItem[field.value]"
                        :items="formSelection(field.value)"
                        :label="field.text"
                      ></v-select>
                    </v-col>
                  </template>
                </v-row>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="close"> Cancel </v-btn>
              <v-btn color="blue darken-1" text @click="save"> Save </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card>
            <v-card-title class="headline">
              Are you sure you want to delete this item?
            </v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="closeDelete"
                >Cancel</v-btn
              >
              <v-btn color="blue darken-1" text @click="deleteItemConfirm"
                >OK</v-btn
              >
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-toolbar>
      <v-divider></v-divider>
    </template>
    <template #[`item.actions`]="{ item }">
      <v-icon small class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
      <v-icon small @click="deleteItem(item)"> mdi-delete </v-icon>
    </template>
  </v-data-table>
</template>

<script>
import { mapState } from 'vuex'
export default {
  props: {
    title: {
      type: String,
      required: false,
      default: 'CRUD Table',
    },
    source: {
      type: Array,
      required: true,
    },
    selectionSources: {
      // [{
      //   key: 'selection value name',
      //   data: [{
      //     text: 'display label',
      //     value: 'value'
      //   }]
      // }]
      type: Array,
      default: null,
    },
    disables: {
      type: Array,
      default: null,
    },
    apiUrl: {
      type: String,
      required: true,
    },
    apiOptionalEpt: {
      type: String,
      default: '',
    },
  },
  data: () => ({
    dialog: false,
    dialogDelete: false,
    editedIndex: -1,
    editedItem: {},
  }),

  computed: {
    ...mapState(['user']),

    formTitle() {
      return (
        (this.editedIndex === -1 ? 'New Item: ' : 'Edit Item: ') + this.title
      )
    },
    headers() {
      const headers = []
      if (this.source.length) {
        const keys = Object.keys(this.source[0])
        for (const key of keys) {
          if (this.disables && !this.disables.includes(key)) {
            let header = {}
            header = {
              text: key[0].toUpperCase() + key.substring(1),
              value: key,
              sortable: true,
            }
            if (this.selectionSources)
              for (const source of this.selectionSources) {
                if (source.key === key) {
                  header.type = 'select'
                }
              }
            headers.push(header)
          }
        }
        // add actions slot
        headers.push({ text: 'Actions', value: 'actions', sortable: false })
        // // // // console.log({ headers, data: this.tableData, default: this.defaultItem })
      }

      return headers
    },
    tableData() {
      let data = []
      if (this.source.length) {
        data = [...this.source]
        // // // // console.log({ data })
        if (this.disables !== null) {
          for (const iIndex in data) {
            for (const index in this.disables) {
              const text = this.disables[index]
              delete data[iIndex][text]
              // // // // console.log({ index, iIndex, text })
            }
          }
        }
        // reformat data to be used

        // // // // console.log({ data })
      }
      return data
    },
    defaultItem() {
      const item = {}
      for (const key of this.headers) {
        if (key.value !== 'actions') item[key.value] = ''
      }
      // // // console.log({ defaultItem: item })

      return item
    },
  },

  watch: {
    dialog(val) {
      val || this.close()
    },
    dialogDelete(val) {
      val || this.closeDelete()
    },
  },

  created() {
    // this.initialize()
  },

  methods: {
    formSelection(key) {
      let items = []
      for (const source of this.selectionSources) {
        if (source.key === key) {
          items = source.data
        }
      }
      return items
    },
    editItem(item) {
      // // // // console.log({ selectedItem: item })
      this.editedIndex = this.tableData.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
      // // console.log({ editedItem: this.editedItem })
    },

    deleteItem(item) {
      this.editedIndex = this.tableData.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialogDelete = true
    },

    async deleteItemConfirm() {
      // call to api to update data
      if (this.apiOptionalEpt) {
        await this.$axios
          .$put(
            this.apiUrl +
              '/' +
              this.editedItem.id +
              (this.apiOptionalEpt.length > 0 ? '/' + this.apiOptionalEpt : '')
          )
          .then((result) => {
            if (result.status !== 'Succeeded') alert(result.message)
            else {
              // // console.log({ result })

              this.tableData.splice(this.editedIndex, 1)
              // update to statusCode
              this.$emit('dataUpdated', this.tableData)

              this.closeDelete()
            }
          })
      } else {
        await this.$axios
          .$delete(this.apiUrl + '/' + this.editedItem.id)
          .then((result) => {
            if (result.status !== 'Succeeded') alert(result.message)
            else {
              // // console.log({ result })

              this.tableData.splice(this.editedIndex, 1)
              // update to statusCode
              this.$emit('dataUpdated', this.tableData)

              this.closeDelete()
            }
          })
      }
    },

    close() {
      this.dialog = false
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },

    closeDelete() {
      this.dialogDelete = false
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },

    async save() {
      this.editedItem.createBy = this.user.id
      this.editedItem.updateBy = this.user.id
      // console.log({ submitData: this.editedItem })
      if (this.selectionSources)
        for (const source of this.selectionSources) {
          if (source.key === 'categoryId') {
            const item = source.data.find(
              (category) => category.value === this.editedItem.categoryId
            )
            // // // console.log({ item })
            this.editedItem.categoryCode = item.text
            if (item) break
          }
        }
      if (this.editedIndex > -1) {
        // console.log({ submitData: this.editedItem })
        await this.$axios
          .$put(this.apiUrl + '/' + this.editedItem.id, this.editedItem)
          .then((result) => {
            if (result.status !== 'Succeeded') alert(result.message)
            else {
              // // // console.log({ result })
            }
          })
        Object.assign(this.tableData[this.editedIndex], this.editedItem)
      } else {
        // console.log({ submitData: this.editedItem })
        // call to api to update data
        await this.$axios.$post(this.apiUrl, this.editedItem).then((result) => {
          if (result.status !== 'Succeeded') alert(result.message)
          else {
            // // // console.log(result)
            const data = result.message.split(' ')
            this.editedItem.id = data[1]
          }
        })

        // then push new data to the cache
        this.tableData.push(this.editedItem)
      }
      // update to statusCode
      this.$emit('dataUpdated', this.tableData)

      this.close()
    },
  },
}
</script>
<style lang="scss">
.vmo-d-table .v-data-table__wrapper .text-start {
  max-width: 100px !important;
  text-overflow: ellipsis;
  overflow: hidden;
  max-height: 100px !important;
}
</style>
