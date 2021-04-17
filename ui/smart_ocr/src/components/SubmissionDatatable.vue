<template>
  <div>
    <md-table v-model="searched" md-sort="name" md-sort-order="asc" md-card md-fixed-header>
      <md-table-toolbar>
        <div class="md-toolbar-section-start">
          <h1 class="md-title">Pending Transcripts</h1>
        </div>

        <md-field md-clearable class="md-toolbar-section-end">
          <md-input placeholder="Search by name..." v-model="search" @input="searchOnTable" />
        </md-field>
      </md-table-toolbar>

      <md-table-empty-state
        md-label="No users found"
        :md-description="`No user found for this '${search}' query. Try a different search term or create a new user.`">
        <md-button class="md-primary md-raised" @click="newUser">Create New User</md-button>
      </md-table-empty-state>

      <md-table-row slot="md-table-row" slot-scope="{ item }" @click.native="openTranscript(item)" v-bind:class="{'checked-out': item.CHECKED_OUT}">
          <md-table-cell md-label="Time" md-sort-by="TIMESTAMP" md-numeric>{{ item.TIMESTAMP }}</md-table-cell>
          <md-table-cell md-label="First" md-sort-by="FIRST_NAME">{{ item.FIRST_NAME }}</md-table-cell>
          <md-table-cell md-label="Middle" md-sort-by="MIDDLE_NAME">{{ item.MIDDLE_NAME }}</md-table-cell>
          <md-table-cell md-label="Last" md-sort-by="LAST_NAME">{{ item.LAST_NAME }}</md-table-cell>
          <md-table-cell md-label="PSU ID" md-sort-by="PSU_ID">{{ item.PSU_ID }}</md-table-cell>
          <md-table-cell md-label="Campus" md-sort-by="CAMPUS">{{ item.CAMPUS }}</md-table-cell>
          <md-table-cell md-label="Residency" md-sort-by="CITIZENSHIP">{{ item.CITIZENSHIP }}</md-table-cell>
      </md-table-row>
    </md-table>
  </div>
</template>

<script>
  const toLower = text => {
    return text.toString().toLowerCase()
  }

  const searchByName = (items, term) => {
    if (term) {
      return items.filter(item => toLower(item.name).includes(toLower(term)))
    }

    return items
  }

  export default {
    name: 'SubmissionDatatable',
    props: {
      queue: {
        type: Array,
        required: true
      },
    },
    data: () => ({
      search: null,
      searched: []
    }),
    methods: {
      newUser () {
        window.alert('Not yet implemented')
      },
      searchOnTable () {
        this.searched = searchByName(this.$props.queue, this.search)
      },
      openTranscript (item) {
        if(item.CHECKED_OUT){
          alert("This transcript is currently checked out by someone else and cannot be opened");
          return;
        }
        // Dispatch the check out event with the selected PSU ID
        this.$store.dispatch('checkOut', item.DOCUMENT_ID);
        // Navigate to the transcript viewer with the transcript object as the payload
        this.$router.push({ name : 'TranscriptViewer', params: { transcript: item} })
      }
    },
    created () {
      this.searched = this.$props.queue
    },
    watch: {
      queue: {
        immediate: true,
        deep: true,
        handler(val, oldVal){
          this.searched = val
        }
      }
    }
  }
</script>

<style scoped>
  .md-field {
    max-width: 300px;
  }
  .checked-out{
    background-color: rgba(255, 0, 0, 0.5);
    color: rgba(0,0,0,0.5);
  }
</style>
