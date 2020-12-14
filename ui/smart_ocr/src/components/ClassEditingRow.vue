<template>
  <md-list-item>
    <md-card>
      <md-card-content>
        <div class="md-layout md-alignment-center-left">
          <div class="md-layout-item md-size-90">
            <div class="md-layout md-alignment-center-left">
              <div class="md-layout-item md-size-50">
                <md-field>
                  <label>Course Name</label>
                  <md-input v-model="course.name"></md-input>
                </md-field>
              </div>
              <div class="md-layout-item md-size-25">
                <md-field>
                  <label>Grade</label>
                  <md-input v-model="course.grade"></md-input>
                </md-field>
              </div>
              <div class="md-layout-item md-size-25">
                <md-field>
                  <label>Credits</label>
                  <md-input v-model="course.credits"></md-input>
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
          <div class="md-layout-item md-size-10">
            <md-button class="md-icon-button">
              <md-icon>delete</md-icon>
            </md-button>
          </div>
        </div>
      </md-card-content>
    </md-card>
  </md-list-item>
</template>

<script>
export default {
  name: "ClassEditingRow",
  props: ["course"],
  data () {
    return {
      course: this.course
    }
  }
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
</style>
