<template>
  <div>
    <md-table v-model="searched" md-sort="name" md-sort-order="asc" md-card md-fixed-header>
      <md-table-toolbar>
        <div class="md-toolbar-section-start">
          <h1 class="md-title">Pending Transcripts</h1>
        </div>
        <!--Make a div that when input= "searchCampusOnTable" and another div input="searchResidencyOnTable" 
        count the amount for each of the things listed 
        <div class="md-toolbar-section-end">
          <md-field md-clearable class="md-toolbar-section-start">
            
            <p>{{ countCampusOnTable() }} results found</p>
            <p>{{ countResidencyOnTable() }} results found</p>
          </md-field> 
        </div> -->

        <md-field md-clearable class="md-toolbar-section-end">
          <md-input placeholder="Search" v-model="search" @input="searchOnTable" />
        </md-field>
        <md-field md-clearable class="md-toolbar-section-end">
          <label for="fields">Select Fields to Search</label>
          <md-select v-model="searchFields" name="fields" id="fields" multiple @input="searchOnTable">
            <md-option value="TIMESTAMP">Date</md-option>
            <md-option value="FIRST_NAME">First Name</md-option>
            <md-option value="MIDDLE_NAME">Middle Name</md-option>
            <md-option value="LAST_NAME">Last Name</md-option>
            <md-option value="PSU_ID">PSU ID</md-option>
            <md-option value="CAMPUS">Campus</md-option>
            <md-option value="CITIZENSHIP">Citizenship</md-option>
          </md-select>
        </md-field>
      </md-table-toolbar>

      <md-table-empty-state
        md-label="No users found"
        :md-description="`No user found for this '${search}' query. Try a different search term or create a new user.`">
        <md-button class="md-primary md-raised" @click="newUser">Create New User</md-button>
      </md-table-empty-state>

      <md-table-row slot="md-table-row" slot-scope="{ item }" @click.native="openTranscript(item)" v-bind:class="{'checked-out': item.CHECKED_OUT}">
          <md-table-cell md-label="Date" md-sort-by="TIMESTAMP" md-numeric>{{ new Date(item.TIMESTAMP).toLocaleDateString() }}</md-table-cell>
          <md-table-cell md-label="Time">{{ new Date(item.TIMESTAMP).toLocaleTimeString() }}</md-table-cell>
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

  const searchByFields = (items, term, fields) => {
    if (term) {
      let searchedFieldArrays = [];
      fields.forEach((field) => {
        searchedFieldArrays.push(items.filter(item => toLower(item[field]).includes(toLower(term))));
      });
      let searchedItems = [];
      searchedFieldArrays.forEach((fieldArray) => {
        fieldArray.forEach((item) => {
          if(searchedItems.filter(e => e.DOCUMENT_ID === item.DOCUMENT_ID).length === 0){
            searchedItems.push(item);
          }
        });
      });
      return searchedItems;
    }

    return items
  }
  const searchByPSUId = (items,term) => {
    if (term) {
      return items.filter(item => toLower(item.psuId).includes(toLower(term)))
    }
    return items
  }

  const searchByCampus = (items,term) => {
    if (term) {
      return items.filter(item => toLower(item.campus).includes(toLower(term)))
    }
    return items
  }
  const searchByResidency = (items,term) => {
    if (term) {
      return items.filter(item => toLower(item.residency).includes(toLower(term)))
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
      searchFields: [],
      transcripts: [],
      searched: []
    }),
    methods: {
      newUser () {
        window.alert('Not yet implemented')
      },
      searchOnTable () {
        if(this.search && this.searchFields.length > 0){
          this.searched = searchByFields(this.transcripts, this.search, this.searchFields);
        } else {
          this.searched = this.transcripts;
        }
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
          this.transcripts = val;
          if(this.searchFields.length === 0){
            this.searched = val;
          }
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
  .white {
    background-color: white !important;
  }
</style>
