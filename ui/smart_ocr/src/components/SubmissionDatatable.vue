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

      <md-table-row slot="md-table-row" slot-scope="{ item }" @click.native="openTranscript(item)">
          <md-table-cell md-label="ID" md-sort-by="id" md-numeric>{{ item.id }}</md-table-cell>
          <md-table-cell md-label="Name" md-sort-by="name">{{ item.name }}</md-table-cell>
          <md-table-cell md-label="Email" md-sort-by="psuId">{{ item.psuId }}</md-table-cell>
          <md-table-cell md-label="Campus" md-sort-by="campus">{{ item.campus }}</md-table-cell>
          <md-table-cell md-label="Residency" md-sort-by="residency">{{ item.residency }}</md-table-cell>
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
    data: () => ({
      search: null,
      searched: [],
      users: [
        {
          id: 1,
          name: "Shawna Dubbin",
          psuId: "sdd0192",
          campus: "University Park",
          residency: "PA"
        },
        {
          id: 2,
          name: "Odette Demageard",
          psuId: "odd9485",
          campus: "University Park",
          residency: "GA"
        },
        {
          id: 3,
          name: "Vera Taleworth",
          psuId: "vtt7456",
          campus: "Altoona",
          residency: "PA"
        },
        {
          id: 4,
          name: "Lonnie Izkovitz",
          psuId: "liz9822",
          campus: "Berks",
          residency: "CO"
        },
        {
          id: 5,
          name: "Thatcher Stave",
          psuId: "tst5667",
          campus: "University Park",
          residency: "PA"
        },
      ]
    }),
    methods: {
      newUser () {
        window.alert('Not yet implemented')
      },
      searchOnTable () {
        this.searched = searchByName(this.users, this.search)
      },
      openTranscript (item) {
        this.$router.push({ name: 'TranscriptViewer', params: {id: item.psuId}});
      }
    },
    created () {
      this.searched = this.users
    }
  }
</script>

<style scoped>
  .md-field {
    max-width: 300px;
  }
</style>
