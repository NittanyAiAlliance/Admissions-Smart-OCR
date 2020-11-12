// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import '@progress/kendo-ui'
import '@progress/kendo-theme-material/dist/all.css'
import { Pager, ListView, ListViewInstaller } from "@progress/kendo-listview-vue-wrapper";

Vue.config.productionTip = false
Vue.use(ListViewInstaller);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App, Pager, ListView },
  template: '<App/>'
})
