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
        <md-card v-for="transcript in transcripts" :key="transcript.PSU_ID">
          <router-link :to="{ name: 'TranscriptViewer', params: {id: transcript.PSU_ID}}">
            <md-card-content>
              <p>{{transcript.PSU_ID}}</p>
              <p>{{transcript.TIMESTAMP}}</p>
            </md-card-content>
          </router-link>
        </md-card>
      </md-app-content>
    </md-app>
  </div>
</template>

<script>
import AppNavDrawer from './AppNavDrawer'
import get from '@/get';
export default {
  name: 'SubmissionBrowser',
  components: {AppNavDrawer},
  data () {
    return {
      menuVisible: false,
      transcripts: []
    }
  },
  created() {
    get.getTranscriptQueue().then(transcripts =>{
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
