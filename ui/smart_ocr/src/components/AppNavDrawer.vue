<template>
  <md-list>
    <md-list-item to="/">
      <md-icon class="nav-icon">dashboard</md-icon>
      <span class="md-list-item-text">Dashboard</span>
    </md-list-item>
    <md-list-item to="/queue">
      <md-icon class="nav-icon">queue</md-icon>
      <span class="md-list-item-text">Transcript Queue</span>
    </md-list-item>
    <md-list-item to="/">
      <md-icon class="nav-icon">history</md-icon>
      <span class="md-list-item-text">Transcript History</span>
    </md-list-item>
    <md-list-item to="/">
      <md-icon class="nav-icon">settings</md-icon>
      <span class="md-list-item-text">Settings</span>
    </md-list-item>
  </md-list>
</template>

<script>
export default {
  name: 'AppNavDrawer'
}
</script>

<style scoped>
  .nav-icon{
    color: #002D62 !important;
  }
</style>
