<template>
  <div id="list-view">
    <kendo-listview :data-source="dataSource" :template="template"></kendo-listview>
  </div>
</template>

<script>
export default {
  name: 'SubmissionBrowser',
  data () {
    return {
      dataSource: [
        {
          firstname: "Joel",
          lastname: "Seidel",
          psuid: "jds6294",

        },
        {
          firstname: "Nittany",
          lastname: "Lion",
          psuid: "nxl9999"
        }
      ],
      template: `
        <div class="card">
            <div class="card-body">
                <span>First Name: </span> #:firstname#
                <span>Last Name: </span>#:lastname#
                <span>PSU ID: </span>#:psuid#
            </div>
        </div>
        `
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
.k-listview {
  background-color: whitesmoke;
}

.card {
  background: #fff;
  font-size: 16px;
  width: 75%;
  min-width: 286px;
  margin: 2em auto;
  padding: 1.25em;
  border-radius: 4px;
  box-shadow: 0 3px 1px -2px rgba(0,0,0,0.2), 0 2px 2px 0 rgba(0,0,0,0.14), 0 1px 5px 0 rgba(0,0,0,0.12);
}

.card-body span {
  width: 100px;
  font-weight: bold;
}
</style>
