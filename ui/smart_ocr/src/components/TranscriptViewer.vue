<template>
    <div class="page-container">
      <md-app md-waterfall md-mode="fixed">
        <md-app-toolbar class="md-primary">
          <md-button class="md-icon-button" @click="menuVisible = !menuVisible">
            <md-icon>menu</md-icon>
          </md-button>
          <span class="md-title">Viewing Transcript: {{this.$route.params.id}}</span>
        </md-app-toolbar>
        <md-app-drawer :md-active.sync="menuVisible">
          <md-toolbar class="md-transparent" md-elevation="0"></md-toolbar>
          <AppNavDrawer/>
        </md-app-drawer>
        <md-app-content>
          <div class="md-layout">
            <TranscriptPreviewPane />
            <div v-if="isLoading" class="md-layout md-gutter md-alignment-center-center">
              <md-progress-spinner class="md-accent" :md-diameter="100" :md-stroke="10" md-mode="indeterminate"></md-progress-spinner><br/>
            </div>
            <TranscriptEditPane v-if="!isLoading" :transcript="transcript"/>
          </div>
        </md-app-content>
      </md-app>
    </div>
</template>

<script>
import AppNavDrawer from './AppNavDrawer'
import get from '@/get'
import TranscriptPreviewPane from './TranscriptPreviewPane'
import ClassEditingRow from './ClassEditingRow'
import TranscriptEditPane from './TranscriptEditPane'
export default {
  name: 'TranscriptViewer',
  components: {TranscriptEditPane, ClassEditingRow, TranscriptPreviewPane, AppNavDrawer},
  data () {
    return {
      menuVisible: false,
      isLoading: true,
      transcript: {}
    }
  },
  created () {
    get.getQueuedTranscript(this.$route.params.id).then(response => {
      this.transcript = response.data
      this.isLoading = false
    })
  }
}
</script>

<style scoped>
  img{
    width: 100%;
  }
  .md-layout-item{
    padding: 1rem;
  }
  .md-app-content {
    max-height: calc(100vh - 112px) !important;
    overflow-y: hidden !important;
  }
</style>
