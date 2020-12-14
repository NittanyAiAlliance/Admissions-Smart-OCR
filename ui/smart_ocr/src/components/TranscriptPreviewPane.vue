<template>
  <div class="md-layout-item">
    <h1>Transcript Preview</h1>
    <md-content id="transcript-viewer">
      <div v-if="loading" class="md-layout md-gutter md-alignment-center-center">
        <md-progress-spinner class="md-accent" :md-diameter="100" :md-stroke="10" md-mode="indeterminate"></md-progress-spinner><br/>
      </div>
      <div v-if="!loading">
        <h1>TRANSCRIPT PREVIEW</h1>
      </div>
    </md-content>
  </div>
</template>

<script>
export default {
  name: "TranscriptPreviewPane",
  data () {
    return {
      previewImages: [],
      loading: true
    }
  },
  props : {
    image: {
      type: String
    }
  },
  created() {

  }
}
</script>

<style scoped>
  #transcript-viewer {
    margin-top: 3rem;
  }
</style>
