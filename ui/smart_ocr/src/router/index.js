import Vue from 'vue'
import Router from 'vue-router'
import SubmissionBrowser from '@/components/SubmissionBrowser'
import SearchTranscripts from '@/components/SearchTranscripts'
import Dashboard from '@/components/Dashboard'
import TranscriptViewer from '@/components/TranscriptViewer';

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Dashboard
    },
    {
      path: '/queue',
      name: 'Queue',
      component: SubmissionBrowser
    },
    {
      path: '/search',
      name: 'Search',
      component: SearchTranscripts
    },
    {
      path: '/transcript',
      name: 'TranscriptViewer',
      component: TranscriptViewer
    }
  ]
})
