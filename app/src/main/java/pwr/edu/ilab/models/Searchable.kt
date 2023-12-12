package pwr.edu.ilab.models

interface Searchable {
    fun doesMatchSearchQuery(query: String): Boolean
}