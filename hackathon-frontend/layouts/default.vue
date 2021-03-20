<template>
  <v-app dark>
    <v-navigation-drawer
      v-model="drawer"
      :mini-variant.sync="miniVariant"
      :clipped="clipped"
      fixed
      app
      dark
    >
      <v-list-item class="px-2 py-md-1 orange">
        <v-list-item-avatar>
          <v-img src="https://randomuser.me/api/portraits/men/85.jpg"></v-img>
        </v-list-item-avatar>

        <v-list-item-title>{{
          user.firstName + ' ' + user.lastName
        }}</v-list-item-title>

        <v-btn icon @click.stop="miniVariant = !miniVariant">
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>
      </v-list-item>

      <v-divider></v-divider>

      <v-list class="pt-0">
        <v-list-item
          v-for="(item, i) in items"
          :key="i"
          :to="item.to"
          router
          exact
        >
          <v-list-item-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="item.title" />
          </v-list-item-content>
        </v-list-item>
      </v-list>

      <v-divider></v-divider>

      <v-list v-if="isAdmin" class="pt-0">
        <v-list-item
          v-for="(item, i) in adminItems"
          :key="i"
          :to="item.to"
          router
          exact
        >
          <v-list-item-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="item.title" />
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-app-bar :clipped-left="clipped" fixed app color="orange">
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-btn v-if="miniVariant" icon @click.stop="miniVariant = !miniVariant">
        <v-icon>mdi-chevron-right</v-icon>
      </v-btn>
      <v-toolbar-title>
        <v-img
          class="align-content-end vmo-logo"
          contain
          src="https://admin.google.com/u/0/ac/images/logo.gif?uid=114429222655118256687"
        ></v-img>
        <div class="vmo-title white--text">{{ title }}</div>
      </v-toolbar-title>
      <v-spacer />
      <v-spacer />
      <v-btn text plain @click="dark = !dark">
        <span class="optional">{{ dark ? 'Light' : 'Dark' }} Mode &nbsp;</span>
        <v-icon>
          mdi-{{ dark ? 'white-balance-sunny' : 'moon-waxing-crescent' }}
        </v-icon>
      </v-btn>
    </v-app-bar>
    <v-main>
      <v-container fluid>
        <nuxt />
      </v-container>
    </v-main>
    <v-footer :absolute="!fixed" app dark>
      <span>&copy; {{ new Date().getFullYear() }} VMO JP - [C10] SCA Team</span>
    </v-footer>
  </v-app>
</template>

<script>
import { mapState } from 'vuex'
export default {
  data: () => ({
    clipped: false,
    drawer: true,
    fixed: true,
    miniVariant: false,
    dark: false,
    items: [
      {
        icon: 'mdi-car-back',
        title: 'Car Listing',
        to: '/rental-cars',
      },
      {
        icon: 'mdi-apps',
        title: 'User Dashboard',
        to: '/',
      },
    ],
    adminItems: [
      {
        icon: 'mdi-apps',
        title: 'Admin Dashboard',
        to: '/admin',
      },
    ],
    title: 'Global Car Sharing',
  }),

  computed: {
    isAdmin() {
      return this.$store.state.isAdmin
    },
    ...mapState({
      user: 'user',
    }),
  },

  watch: {
    dark() {
      this.$vuetify.theme.dark = this.dark
    },
  },

  created() {
    this.$vuetify.theme.dark = this.dark
  },

  mounted() {},
}
</script>
<style lang="scss">
.vmo-logo,
.vmo-title {
  display: inline-block;
  // height: 38px;
}
.vmo-logo {
  margin-bottom: -10px;
  max-width: 80px;
}
@media only screen and (max-width: 500px) {
  .optional {
    display: none;
  }
  .vmo-logo {
    margin-bottom: -5px;
    max-width: 50px;
  }
  .vmo-title {
    font-size: 80%;
  }
}
@media only screen and (max-width: 320px) {
  .optional {
    display: none;
  }
  .vmo-logo {
    margin: auto;
    margin-bottom: -10px;
    max-width: 80px;
  }
  .vmo-title {
    display: none;
  }
}
</style>
