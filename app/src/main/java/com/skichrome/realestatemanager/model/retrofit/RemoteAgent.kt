package com.skichrome.realestatemanager.model.retrofit

import com.skichrome.realestatemanager.model.database.Agent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteAgent(
    @Json(name = "status") val status: String,
    @Json(name = "system_date") val lastUpdated: String,
    @Json(name = "lang") val lang: String,
    @Json(name = "num_results") val numResults: Int,
    @Json(name = "result") val results: List<AgentResults> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AgentResults(
    @Json(name = "agent_id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "last_database_update") val lastUpdate: Long?
)
{
    companion object
    {
        fun fromRemoteToLocal(agentResults: List<AgentResults>): List<Agent>
        {
            val localAgentList: MutableList<Agent> = mutableListOf()
            agentResults.forEach {
                val agent = Agent(agentId = it.id, name = it.name, lastUpdate = it.lastUpdate)
                localAgentList.add(agent)
            }
            return localAgentList
        }

        fun fromLocalToRemote(agents: List<Agent>): List<AgentResults>
        {
            val remoteAgentList: MutableList<AgentResults> = mutableListOf()
            agents.forEach {
                val agentResult = AgentResults(id = it.agentId, lastUpdate = it.lastUpdate, name = it.name)
                remoteAgentList.add(agentResult)
            }
            return remoteAgentList
        }
    }
}