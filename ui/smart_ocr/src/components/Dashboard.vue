<template>
    <div class="page-container">
      <md-app md-waterfall md-mode="fixed">
        <md-app-toolbar class="md-primary">
          <span class="md-title">SmartOCR Dashboard</span>
        </md-app-toolbar>
        <md-app-drawer md-permanent="full">
          <md-toolbar class="md-transparent" md-elevation="0">
          </md-toolbar>
          <AppNavDrawer/>
        </md-app-drawer>
        <md-app-content>
          <md-card md-with-hover>
            <md-ripple>
              <md-card-header>
                <div class="md-title">Dashboard Component</div>
              </md-card-header>
              <md-card-content>
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Optio itaque ea, nostrum odio. Dolores, sed accusantium quasi non.</p>
              </md-card-content>
              <md-card-actions>
                <md-button>Do Something</md-button>
                <md-button>Do Something Else</md-button>
              </md-card-actions>
            </md-ripple>
          </md-card>
        </md-app-content>
      </md-app>
    </div>
</template>

<script>
import AppNavDrawer from './AppNavDrawer'
export default {
  name: 'Dashboard',
  components: {AppNavDrawer}
}
</script>

<style scoped>
  .md-card {
    width: 100%;
    margin: 4px;
    display: inline-block;
    vertical-align: top;
  }
  .md-app-drawer{
    max-width: 20%;
  }
  .md-icon {
    color: #002D62 !important;
  }
  .md-app-toolbar {
    background-color: #002D62 !important;
  }
  .dash-alert {
    background-color: rgba(255, 0, 0, 0.5);
  }
  .dash-warning {
    background-color: rgba(240, 173, 78, 0.5);
  }
</style>
