<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  components: {}
}
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    height: 100vh;
    width: 100vw;
  }
  .md-app{
    height: 100vh;
    width: 100vw;
  }
  a span{
    color: #002D62 !important;
  }

  .md-primary {
    background-color: #002D62 !important;
  }
</style>
