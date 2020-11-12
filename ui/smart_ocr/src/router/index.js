import Vue from 'vue'
import Router from 'vue-router'
import SubmissionBrowser from '@/components/SubmissionBrowser'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: SubmissionBrowser
    }
  ]
})
