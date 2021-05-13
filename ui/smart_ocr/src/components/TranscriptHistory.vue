<template>
   <div>
     <md-table v-model="logs" md-sort="name" md-sort-order="asc" md-card md-fixed-header>
        <md-table-toolbar>
            <div class="md-toolbar-section-start">
                <h1 class="md-title">Transcript History</h1>
            </div>
            <!--
            <div class="md-toolbar-section-start">
                <md-field md-clearable class="md-toolbar-section-start">
                    <md-input placeholder="Title..." v-model="search" @input="searchTitleOnTable" />
                </md-field>

                <md-field md-clearable class="md-toolbar-section-start">
                    <md-input placeholder="PSU ID..." v-model="search" @input="searchPSUIdOnTable"/>
                </md-field>

                <md-field md-clearable class="md-toolbar-section-start">
                    <md-input placeholder="Description..." v-model="search" @input="searchDescriptionOnTable"/>
                </md-field>

                <md-field md-clearable class="md-toolbar-section-start">
                    <md-input placeholder="Timestamp..." v-model="search" @input="searchTimestampOnTable"/>
                </md-field>
            </div>-->
        </md-table-toolbar>
        <!--<md-table-empty-state
            md-label="No users found"
            :md-description="`No user found for this '${search}' query. Try a different search term or create a new user.`">
            <md-button class="md-primary md-raised" @click="newUser">Create New User</md-button>
      </md-table-empty-state> -->

        <md-table-row slot="md-table-row" slot-scope="{ item }">
          <md-table-cell md-label="Timestamp" md-sort-by="timestamp">{{ item.timestamp }}</md-table-cell>
          <md-table-cell md-label="User" md-sort-by="uid">{{ item.uid }}</md-table-cell>
          <md-table-cell md-label="Document ID" md-sort-by="did">{{ item.did }}</md-table-cell>
          <md-table-cell md-label="Title" md-sort-by="title">{{ item.title }}</md-table-cell>
          <md-table-cell md-label="Description" md-sort-by="description">{{ item.description }}</md-table-cell>
        </md-table-row>
    </md-table>
   </div>
</template>

<!-- Define function for searching PSU ID, Log Title, timestamp -->
<script>

const toLower = text => {
    return text.toString().toLowerCase()
  }

  const searchByTitle = (items, term) => {
    if (term) {
      return items.filter(item => toLower(item.title).includes(toLower(term)))
    }

    return items
  }
  const searchByPSUId = (items,term) => {
    if (term) {
      return items.filter(item => toLower(item.psuId).includes(toLower(term)))
    }
    return items
  }

  const searchByDescription = (items,term) => {
    if (term){
      return items.filter(item => toLower(item.description).includes(toLower(term)))
    }
  }

  const searchByTimestamp = (items,term) => {
    if (term) {
      return items.filter(item => toLower(item.timestamp).includes(toLower(term)))
    }
    return items
  }





//import { defineComponent } from '@vue/composition-api'
//Date picker
export default {
    name: 'TranscriptHistory',
    data: () => ({
        search: null,
        searched: []
    }),
    methods: {
        searchTitleOnTable() {
            this.searched  = searchByTitle(this.logs, this.search)
        },
        searchPSUIdOnTable(){
            this.searched = searchByPSUId(this.logs, this.search)
        },
        searchTimestampOnTable(){
            this.searched = searchByTimestamp(this.logs, this.search)
        },
        searchDescriptionOnTable(){
          this.searched = searchByDescription(this.logs, this.search)
        }
    },
    created () {
      this.$store.dispatch('fetchInteractionLogs');
    },
    computed: {
      logs() {
        return Array.from(this.$store.state.interactionLogs.values())
      }
    }
}
</script>
<style scoped>
  .md-field {
    max-width: 300px;
  }
</style>

