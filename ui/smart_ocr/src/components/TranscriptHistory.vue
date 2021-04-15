<template>
   <div>
     <md-table v-model="searched" md-sort="name" md-sort-order="asc" md-card md-fixed-header>
        <md-table-toolbar>
            <div class="md-toolbar-section-start">
                <h1 class="md-title">Transcript History</h1>
            </div>
   
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
            </div>
        </md-table-toolbar>
        <!--
        <md-table-empty-state
            md-label="No users found"
            :md-description="`No user found for this '${search}' query. Try a different search term or create a new user.`">
            <md-button class="md-primary md-raised" @click="newUser">Create New User</md-button>
      </md-table-empty-state> -->

        <md-table-row slot="md-table-row" slot-scope="{ item }">
          <md-table-cell md-label="ID" md-sort-by="id" md-numeric>{{ item.id }}</md-table-cell>
          <md-table-cell md-label="Title" md-sort-by="title">{{ item.title }}</md-table-cell>
          <md-table-cell md-label="PSU ID" md-sort-by="psuId">{{ item.psuId }}</md-table-cell>        
          <md-table-cell md-label="Description" md-sort-by="description">{{ item.description }}</md-table-cell>
          <md-table-cell md-label="Timestamp" md-sort-by="timestamp">{{ item.timestamp }}</md-table-cell>
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
        searched: [],
        logs: [
            {
                id: 1,
                title: "Title 1",
                psuId: 99999999,
                description: "Description 1",
                timestamp: "May 20"
            },
            {
                id: 2,
                title: "Title 2",
                psuId: 88888888,
                description: "Description 2",
                timestamp: "May 21"
            },
            {
                id: 3,
                title: "Title 3",
                psuId: 77777777,
                description: "Description 3",
                timestamp: "May 22"
            },
            {
                id: 4,
                title: "Title 4",
                psuId: 66666666,
                description: "Description 4",
                timestamp: "May 23"
            },
        ]
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
      this.searched = this.logs
    }

}
</script>
<style scoped>
  .md-field {
    max-width: 300px;
  }
</style>

