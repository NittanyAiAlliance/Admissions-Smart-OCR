<template>
  <div class="md-layout-item">
    <div v-if="loading" class="md-layout md-gutter md-alignment-center-center">
      <md-progress-spinner class="md-accent" :md-diameter="100" :md-stroke="10" md-mode="indeterminate"></md-progress-spinner><br/>
    </div>
    <div v-if="!loading" class="md-layout md-gutter md-alignment-center-center" id="transcript-viewer">
      <img id="transcript-img" src="@/assets/script.png"/>
    </div>
  </div>
</template>

<script>
export default {
  name: "TranscriptPreviewPane",
  data () {
    return {
      previewImages: [],
      loading: false
    }
  }
}
</script>

<style scoped>
  #transcript-img{
    max-height: calc(100vh - 112px);
    width: auto;
    border: 1px solid rgb(0,0,0);
    box-shadow: 0 0 10px rgba(0,0,0,0.4);
  }
</style>
