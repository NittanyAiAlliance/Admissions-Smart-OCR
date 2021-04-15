<template>
      <div class="page-container">
          <md-app md-mode="reveal">
              <md-app-toolbar class="md-primary">
                  <md-button class="md-icon-button" @click="menuVisible = !menuVisible">
                      <md-icon>menu</md-icon>
                  </md-button>
                  <span class="md-title">Transcript History</span>
              </md-app-toolbar>

            <md-app-drawer :md-active.sync="menuVisible">
                <md-toolbar class="md-transparent" md-elevation="0"></md-toolbar>
                <AppNavDrawer />
            </md-app-drawer>
            <md-app-content>
                <!-- Actually putting in the transcript history -->
                <TranscriptHistory />  
            </md-app-content>
          </md-app>
      </div>
</template>
<script>
import AppNavDrawer from './AppNavDrawer'
import TranscriptHistory from './TranscriptHistory'



export default {
    name: 'LogBrowser',
    components: {AppNavDrawer,TranscriptHistory},

    data () {
    return {
      menuVisible: false,
    }}
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

