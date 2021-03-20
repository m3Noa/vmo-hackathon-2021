// no need to import or export Vue/vuex, Nuxt will do that for us
import api from '@/config/api'

export const state = () => ({
  // contains mockup data to test UI
  // will be remove when connecting to APIs
  dataStatus: '',
  isAdmin: false,
  user: {
    firstName: 'Tony',
    lastName: 'Ngo',
    address: 'Viet Nam',
    mobilephone: '+84123456789',
    updateAt: '2021-03-20 23:13:00+0800',
    updateBy: 'admin',
    id: '41f1eef8-8657-4f4c-929c-719cef82db21',
    isActive: 1,
    isDeleted: 0,
    createAt: '2021-03-20 21:12:25+0800',
    createBy: 'admin',
  },
  categoryList: [],
  customerList: [],
  rentalList: [],
  itemList: [],
  userRentalList: [],
  userItemList: [],
  rangeDate: [],
  selectedItem: {
    id: '422b7f2b-8576-44e7-a64d-f71e3163da75',
    categoryId: 'a15539a0-f483-49fb-97c1-911af9adea6b',
    name: 'Honda Civic 2021',
    description:
      "You would be forgiven for not recognizing the 2021 Honda Civic. While the name 'Civic' may return images of economy-sized penalty boxes, today s Civic has more in common with the Accord than it does with yesteryear s sin bins.",
    imageUrl:
      'https://images.hgmsites.net/lrg/2020-honda-civic-manual-angular-rear-exterior-view_100756420_l.jpg',
    pricePerDay: 125.0,
    isActive: 1,
    rentalStatus: 0,
    category: {
      id: 'a15539a0-f483-49fb-97c1-911af9adea6b',
      code: 'SEDAN',
      name: 'SEDAN',
      isActive: 1,
      isDeleted: 0,
      createAt: '2021-03-20 12:00:09+0700',
      createBy: 'system',
      updateAt: '2021-03-20 22:49:17+0800',
      updateBy: 'admin',
    },
    createAt: '2021-03-20 23:26:05+0800',
    createBy: '41f1eef8-8657-4f4c-929c-719cef82db21',
    updateAt: '2021-03-20 23:26:05+0800',
    updateBy: 'system',
  },
})

export const mutations = {
  setItemList(state, itemList) {
    state.itemList = [...itemList]
  },
  setUserItemList(state, itemList) {
    state.userItemList = [...itemList]
  },
  setStatus(state, dataStatus) {
    state.dataStatus = dataStatus
  },
  setCategoryList(state, categoryList) {
    state.categoryList = [...categoryList]
  },
  setCustomerList(state, customerList) {
    state.customerList = [...customerList]
  },
  setRentalList(state, rentalList) {
    state.rentalList = [...rentalList]
  },
  setUserRentalList(state, rentalList) {
    state.userRentalList = [...rentalList]
  },
  setSelectedItem(state, selectedItem) {
    state.selectedItem = { ...selectedItem }
  },
  setRangeDate(state, rangeDate) {
    state.rangeDate = [...rangeDate]
  },
  setAdminState(state, status) {
    state.isAdmin = status
  },
}

export const actions = {
  async updateData({ state, commit }, syncStatus) {
    if (syncStatus && syncStatus !== state.dataStatus) {
      await commit('setStatus', syncStatus)

      const categoriesPayload = await fetch(api.category).then((response) =>
        response.json()
      )
      commit('setCategoryList', categoriesPayload.data)
      // console.log({ 'Category List': state.categoryList })

      const carsPayload = await fetch(api.car).then((response) =>
        response.json()
      )
      commit('setItemList', carsPayload.data)
      // console.log({ 'Car List': state.itemList })

      const userItems = []
      for (const item of carsPayload.data) {
        if (item.createBy === state.user.id) {
          userItems.push(item.id)
        }
      }
      commit('setUserItemList', userItems)
      // console.log({ 'User Car List': state.userItemList })

      const customersPayload = await fetch(api.customer).then((response) =>
        response.json()
      )
      commit('setCustomerList', customersPayload.data)
      // console.log({ 'Customer List': state.customerList })

      const rentalsPayload = await fetch(api.rental).then((response) =>
        response.json()
      )
      commit('setRentalList', rentalsPayload.data)
      // console.log({ 'Rental List': state.itemList })

      const userRentals = []
      for (const rental of rentalsPayload.data) {
        if (
          state.userItemList.includes(rental.carId) ||
          state.user.id === rental.customerId
        ) {
          userRentals.push(rental.id)
        }
      }
      commit('setUserRentalList', userRentals)
      // console.log({ 'User Rental List': state.userItemList })

      commit('setAdminState', true)
    }
  },
  async getSelectedItem({ state, commit }, carId) {
    if (carId !== state.selectedItem.id) {
      const carData = await fetch(api.car + '/' + carId).then((response) =>
        response.json()
      )
      commit('setSelectedItem', carData.data)
      // console.log({ 'Selected Car': state.selectedItem })
    }
  },
}
