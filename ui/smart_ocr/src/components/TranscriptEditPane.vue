<template>
  <div class="md-layout-item" id="edit-pane">
    <md-dialog :md-active.sync="showConfirmSubmit">
      <md-dialog-title>Submit Confirmation</md-dialog-title>
      <md-dialog-content>
        <p>Are you sure you would like to submit this transcript?</p>
      </md-dialog-content>
      <md-dialog-actions>
        <md-button class="alert-warning" @click="showConfirmSubmit = false">No, Go Back</md-button>
        <md-button class="md-primary" @click="handleSubmit">Yes, Confirm Submit</md-button>
      </md-dialog-actions>
    </md-dialog>
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
    </div>
    <div class="md-layout" id="classEditingList">
      <h3>Transcript Courses</h3>
      <md-list>
        <ClassEditingRow
          v-for="course in transcript.COURSES"
          v-bind:course="course"
          v-bind:key="course.name"
          @on-delete="handleDeleteCourse" />
        <md-list-item>
          <div class="md-layout md-alignment-center-center">
            <md-button class="md-icon-button md-raised" @click="handleAddCourse">
              <md-icon>add</md-icon>
            </md-button>
          </div>
        </md-list-item>
      </md-list>
      <div class="md-layout md-alignment-bottom-right">
        <md-button class="md-raised alert-warning">Discard Changes</md-button>
        <md-button class="md-raised md-primary" @click="showConfirmSubmit = true">Submit</md-button>
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
      showConfirmSubmit: false,
      showConfirmDiscardChanges: false,
    };
  },

  methods: {
    /**
     * Handle the submission of a completed transcript
     * @param e event arg object
     */
    handleSubmitClick: function(e){
      this.showConfirmSubmit = true;
    },
    /**
     * Handle discarding all changes made on a transcript
     * @param e event arg object
     */
    handleDiscardChangesClick: function(e){
       this.showConfirmDiscardChanges = true;
    },
    /**
     * Handle adding a course to the transcript DOM
     * @param e event arg object
     */
    handleAddCourse: function(e){
      //Push an empty course into the course transcript array
      this.$props.transcript.COURSES.push({
        name: '',
        grade: '',
        credits: '',
      });
      //Force the component to update and display the new course
      this.$forceUpdate();
    },
    /**
     * Handle a delete course event from a ClassEditingRow DOM object
     * @param props deleted course props
     */
    handleDeleteCourse: function(props) {
      //Find the index value of the deleted class record
      let courseIndex = this.$props.transcript.COURSES.findIndex((course) => {
        return course.name === props.name
      });
      //Remove the course record at the index value
      this.$props.transcript.COURSES.splice(courseIndex, 1);
      //Force the component to update and stop displaying the new course
      this.$forceUpdate();
    },
    /**
     * Handle submission of a completed transcript
     * @param e event arg object
     */
    handleSubmit: function(e) {

    }
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
    max-height: calc(100vh - 96px) !important;
  }
</style>
