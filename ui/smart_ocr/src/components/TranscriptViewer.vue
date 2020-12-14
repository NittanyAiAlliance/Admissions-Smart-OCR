<template>
    <div class="page-container">
      <md-app md-mode="reveal">
        <md-app-toolbar class="md-primary">
          <md-button class="md-icon-button" @click="menuVisible = !menuVisible">
            <md-icon>menu</md-icon>
          </md-button>
          <span class="md-title">Transcript Viewer</span>
        </md-app-toolbar>
        <md-app-drawer :md-active.sync="menuVisible">
          <md-toolbar class="md-transparent" md-elevation="0"></md-toolbar>
          <AppNavDrawer/>
        </md-app-drawer>
        <md-app-content>
          <div class="md-layout">
            <div class="md-layout-item">
              <h1>Transcript Editing</h1>
              <div class="md-layout">
                <div class="md-layout-item">
                  <h3>Student Information</h3>
                  <md-field>
                    <md-input placeholder="First Name"></md-input>
                  </md-field>
                  <md-field>
                    <md-input placeholder="Last Name"></md-input>
                  </md-field>
                  <md-field>
                    <md-input placeholder="PSU ID"></md-input>
                  </md-field>
                  <md-field>
                    <md-input placeholder="Submitted: 2020-12-01 11:53AM" readonly></md-input>
                  </md-field>
                </div>
                <div class="md-layout-item">
                  <h3>High School Information</h3>
                  <md-field>
                    <md-input placeholder="High School Name"></md-input>
                  </md-field>
                  <md-field>
                    <md-input placeholder="Address"></md-input>
                  </md-field>
                  <md-field>
                    <md-input placeholder="Phone Number"></md-input>
                  </md-field>
                  <md-field>
                    <md-input placeholder="CEEB Code"></md-input>
                  </md-field>
                </div>
              </div>
              <div class="md-layout">
                <p>* Class editing rows *</p>
              </div>
            </div>
            <TranscriptPreviewPane />
          </div>
        </md-app-content>
      </md-app>
    </div>
</template>

<script>
import AppNavDrawer from './AppNavDrawer'
import get from '@/get';
import TranscriptPreviewPane from "./TranscriptPreviewPane";
export default {
  name: "TranscriptViewer",
  components: {TranscriptPreviewPane, AppNavDrawer},
  data () {
    return {
      menuVisible: false
    }
  },
  created() {
    get.getQueuedTranscript(this.$route.params.id).then(response => {
    });
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
  #transcript-viewer{
    max-height: 90vh;
  }
  .md-content{
    max-height: 100%;
    overflow: auto;
  }
</style>
