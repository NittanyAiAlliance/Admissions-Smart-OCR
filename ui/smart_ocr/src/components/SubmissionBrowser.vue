<template>
  <div class="page-container">
    <md-app md-mode="reveal">
      <md-app-toolbar class="md-primary">
        <md-button class="md-icon-button" @click="menuVisible = !menuVisible">
          <md-icon>menu</md-icon>
        </md-button>
        <span class="md-title">Transcript Queue</span>
      </md-app-toolbar>

      <md-app-drawer :md-active.sync="menuVisible">
        <md-toolbar class="md-transparent" md-elevation="0"></md-toolbar>
        <AppNavDrawer/>
      </md-app-drawer>

      <md-app-content>
        <div v-if="transcripts.length > 0">
          <md-card v-for="transcript in transcripts" :key="transcript.PSU_ID">
            <router-link :to="{ name: 'TranscriptViewer', params: {id: transcript.PSU_ID}}">
              <md-card-content>
                <p>{{transcript.PSU_ID}}</p>
                <p>{{transcript.TIMESTAMP}}</p>
              </md-card-content>
            </router-link>
          </md-card>
        </div>
        <div v-if="transcripts.length === 0 && !isLoading">
          <md-empty-state
            md-icon="image_search"
            md-label="No Transcripts Found in Queue"
            md-description="Thanks for checking in, maybe there will be some later. Here's a button for something to click on">
            <md-button class="md-primary md-raised">Click Me!</md-button>
          </md-empty-state>
        </div>
        <SubmissionDatatable />
      </md-app-content>
    </md-app>
  </div>
</template>

<script>
import AppNavDrawer from './AppNavDrawer'
import SubmissionDatatable from './SubmissionDatatable'
import get from '@/get';
export default {
  name: 'SubmissionBrowser',
  components: {AppNavDrawer,SubmissionDatatable},

  data () {
    return {
      menuVisible: false,
      isLoading: true,
      transcripts: []
    }
  },
  created() {
    get.getTranscriptQueue().then(transcripts =>{
      this.isLoading = false;
      this.transcripts = transcripts.data.queue;
    });
  }
}
</script>

<style scoped>
  .md-app {
    border: 1px solid rgba(0, 0 0, 0.12);
  }

  .md-drawer {
    width: 25%;
    max-width: calc(100vw - 125px);
  }
  .md-app {
    height: 100vh !important;
    width: 100vw;
  }
</style>
