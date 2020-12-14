<template>
  <div class="md-layout-item" id="edit-pane">
    <h1>Edit Transcript Information</h1>
    <md-divider />
    <div class="md-layout">
      <div class="md-layout-item info-container">
        <h3>Student Information</h3>
        <md-divider/>
        <md-field>
          <md-input v-model="transcript.STUDENT.FIRST_NAME"></md-input>
          <span class="md-helper-text">First Name</span>
        </md-field>
        <md-field>
          <md-input v-model="transcript.STUDENT.LAST_NAME"></md-input>
          <span class="md-helper-text">Last Name</span>
        </md-field>
        <md-field>
          <md-input v-model="transcript.STUDENT.PSU_ID"></md-input>
          <span class="md-helper-text">PSU ID</span>
        </md-field>
        <md-field>
          <md-input v-model="transcript.RECEIVED_TIMESTAMP" readonly></md-input>
          <span class="md-helper-text">Submission Timestamp</span>
        </md-field>
      </div>
      <div class="md-layout-item info-container">
        <h3>High School Information</h3>
        <md-divider/>
        <md-field>
          <md-input v-model="transcript.HIGH_SCHOOL.NAME"></md-input>
          <span class="md-helper-text">High School Name</span>
        </md-field>
        <md-field>
          <md-input v-model="transcript.HIGH_SCHOOL.ADDRESS"></md-input>
          <span class="md-helper-text">Address</span>
        </md-field>
        <md-field>
          <md-input v-model="transcript.HIGH_SCHOOL.PHONE"></md-input>
          <span class="md-helper-text">Phone Number</span>
        </md-field>
        <md-field>
          <md-input v-model="transcript.HIGH_SCHOOL.CEEB"></md-input>
          <span class="md-helper-text">CEEB Code</span>
        </md-field>
      </div>
      <div class="md-layout" id="classEditingList">
        <h3>Transcript Courses</h3>
        <md-list>
          <ClassEditingRow
            v-for="course in transcript.COURSES"
            v-bind:course="course"
            v-bind:key="course.name" />
        </md-list>
      </div>
    </div>
  </div>
</template>

<script>
import ClassEditingRow from "./ClassEditingRow"
export default {
  name: "TranscriptEditPane",
  components: {ClassEditingRow},
  data () {
    return {
      isLoading: true,
    };
  },
  props: ['transcript']
}
</script>

<style scoped>
  .info-container {
    margin: 5px;
  }
  #edit-pane {
    border-left: 2px solid #002D62;
    overflow-y: scroll;
    max-height: calc(100vh - 112px) !important;
  }
</style>
