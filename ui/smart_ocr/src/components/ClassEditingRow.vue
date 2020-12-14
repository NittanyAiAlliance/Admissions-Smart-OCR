<template>
    <md-list-item>
      <md-icon>reorder</md-icon>
      <form>
        <md-field>
          <label>Class Name</label>
          <md-input v-model="type"></md-input>
          <span class="md-helper-text">Class Name</span>
        </md-field>
      </form>
    </md-list-item>
</template>

<script>
export default {
  name: "ClassEditingRow"
}
</script>

<style scoped>

</style>
