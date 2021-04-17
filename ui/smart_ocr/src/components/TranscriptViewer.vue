<template>
    <div class="page-container">
      <md-app md-waterfall md-mode="fixed">
        <md-app-toolbar class="md-primary">
          <md-button class="md-icon-button" @click="menuVisible = !menuVisible">
            <md-icon>menu</md-icon>
          </md-button>
          <span class="md-title">Viewing Transcript: {{this.$route.params.transcript.PSU_ID}}</span>
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
            <TranscriptEditPane v-if="!isLoading" :transcript="transcript" @cancel="handleCancel" @submit="handleSubmit"/>
          </div>
        </md-app-content>
      </md-app>
    </div>
</template>

<script>
import AppNavDrawer from './AppNavDrawer'
import get from '@/get'
import TranscriptPreviewPane from "./TranscriptPreviewPane"
import ClassEditingRow from "./ClassEditingRow"
import TranscriptEditPane from "./TranscriptEditPane"
export default {
  name: "TranscriptViewer",
  components: {TranscriptEditPane, ClassEditingRow, TranscriptPreviewPane, AppNavDrawer},
  data () {
    return {
      menuVisible: false,
      isLoading: true,
      transcript: {},
      submitted: false
    }
  },
  methods: {
    /**
     * Handle user clicks the cancel button
     */
    handleCancel(){
      // Since the action was cancelled, it was not submitted
      this.submitted = false
      // Dispatch the check in event to the store
      this.dispatchCheckIn()
    },
    /**
     * Handle user clicks the submit button
     */
    handleSubmit(){
      // The submit button was clicked, therefore it has been submitted
      this.submitted = true
      // Close this tab
      window.close()
    },
    /**
     * Handle the user closes the window
     */
    handleCloseWindow(){
      // The window was closed so it was not submitted
      this.submitted = false
      // Dispatch the check in event to the store
      this.dispatchCheckIn()
    },
    /**
     * Dispatch check in action for transcript
     */
    dispatchCheckIn(){
      // Dispatch the check in event with the transcript identifier
      this.$store.dispatch('checkIn', this.transcript.PSU_ID);
    }
  },
  created() {
    // Set handler for the window closing
    document.addEventListener('beforeunload', this.handleCloseWindow)
    // Fetch the specified transcript
    get.getQueuedTranscriptResults(this.$route.params.transcript.DOCUMENT_ID).then(response => {
      // Set the local transcript data
      this.transcript = this.$route.params.transcript
      this.transcript.results = response.data
      // Set loading to be done as we have the transcript data now
      this.isLoading = false;
    });
  },
  beforeRouteLeave(to, from, next){
    if(!this.submitted){
      this.dispatchCheckIn();
    }
    next()
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
