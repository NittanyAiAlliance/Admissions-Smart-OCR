<template>
  <md-list-item class="classEditingRow" >
    <div class="md-layout">
      <div class="md-layout-item md-size-10 course-actions-col">
        <md-button class="md-icon-button md-raised md-primary" @click="handleConfirmClick">
          <md-icon v-if="!this.state.isConfirmed">check_circle_outline</md-icon>
          <md-icon v-if="this.state.isConfirmed">edit</md-icon>
        </md-button>
      </div>
      <div class="md-layout-item md-size-90">
        <md-card v-bind:class="this.state">
          <md-card-content>
            <div class="md-layout md-alignment-center-left">
              <div class="md-layout-item md-size-90 course-content">
                <div class="md-layout md-alignment-center-left">
                  <div class="md-layout-item md-size-50">
                    <md-field>
                      <label>Course Name</label>
                      <md-input :value="this.course.name" @change="changeName(item,$event)"></md-input>
                    </md-field>
                  </div>
                  <div class="md-layout-item md-size-25">
                    <md-field>
                      <label>Grade</label>
                      <md-input :value="this.course.grade" @change="changeGrade(item,$event)"></md-input>
                    </md-field>
                  </div>
                  <div class="md-layout-item md-size-25">
                    <md-field>
                      <label>Credits</label>
                      <md-input :value="this.course.credits" @change="changeCredits(item,$event)"></md-input>
                    </md-field>
                  </div>
                </div>
                <div class="md-layout md-alignment-center-left">
                  <div class="md-layout-item">
                    <md-field>
                      <label for="gencourse">Generic Type</label>
                      <md-select name="gencourse" id="gencourse">
                        <md-option value = "business"> Business </md-option>
                        <md-option value = "comp"> Computer Science </md-option>
                        <md-option value = "eng"> English </md-option>
                        <md-option value = "fine"> Fine Arts </md-option>
                        <md-option value = "worldlang"> World Language </md-option>
                        <md-option value = "math"> Mathematics </md-option>
                        <md-option value = "music"> Music </md-option>
                        <md-option value = "science"> Science </md-option>
                        <md-option value = "phys"> Physical Education </md-option>
                        <md-option value = "history"> History/Social Studies </md-option>
                        <md-option value = "other"> Other Subjects </md-option>
                      </md-select>
                    </md-field>
                  </div>
                  <div class="md-layout-item">
                  </div>
                </div>
              </div>
              <div class="md-layout-item md-size-10 course-actions-col">
                <md-button class="md-icon-button md-raised">
                  <md-icon>outlined_flag</md-icon>
                </md-button>
                <md-button class="md-icon-button md-raised md-accent" @click="handleDelete">
                  <md-icon>delete</md-icon>
                </md-button>
              </div>
              
            </div>
          </md-card-content>
        </md-card>
      </div>
    </div>
  </md-list-item>
</template>

<script>
export default {
  name: "ClassEditingRow",
  data: function() {
    return {
      course : Object.freeze(this.course),
      showConfirmDelete : false,
      state: {
        isConfirmed : false
      },
    }
 },

  

  methods: {
    /**
     * Handle delete event from delete button
     * @param e event params
     */
    handleDelete: function(e){
      this.$emit('on-delete', {
        name : this.course.name
      })
    },
    changeName: function(item, event){
      console.log("Old name: " + this.course.name + "New name: " + 
      event.target.value); //Old name and then new name
      this.course.name = event.target.value; //Assignment change
    },

    changeGrade: function(item, event){
      console.log("Old grade value: " + this.course.grade + "New grade value: " + 
      event.target.value); //Old grade and then new grade
      this.course.grade = event.target.value; //Assignment change
    },

    changeCredits: function(item, event){
      console.log("Old credit value: " + this.course.credits + "New credit value: " + 
      event.target.value); //Old value and then new value
      this.course.credits = event.target.value; //Assignment change
    },

   /**
     * Toggle the confirmation of this class editing row on click
     * @param e event params
     */
    handleConfirmClick: function(e){
      //Toggle confirmed state
      this.state.isConfirmed = !this.state.isConfirmed;
    },
  },

  watch: {

  },

  props: ["course"],
}
</script>

<style scoped>
  .md-card {
    padding:0;
    margin:0;
  }
  .md-select{
    width: 100% !important;
  }
  .course-actions-col {
    padding: 4px;
    display: flex;
    flex-flow: wrap;
    align-items: center;
    justify-content: center;
    align-content: center;
  }
  .course-actions-col .md-icon-button{
    margin-top: 8px;
  }
  .md-input{
    margin-left: 4px !important;
  }
  .isConfirmed {
    opacity: 0.3;
    background-color: lightgreen;
  }
  .course-content{
    padding-right: 8px;
  }
</style>
