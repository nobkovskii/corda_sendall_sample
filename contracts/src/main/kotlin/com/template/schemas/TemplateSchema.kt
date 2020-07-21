package com.template.schemas

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

object TemplateSchema
object TemplateSchemaV1 : MappedSchema(
        schemaFamily = TemplateSchema.javaClass,
        version = 1,
        mappedTypes = listOf(TemplateSchemaV1.PersistentTemplate::class.java)
) {
    @Entity
    @Table(name = "template_state")
    class PersistentTemplate(
            @Column(name = "data")
            var data: String
    ) : PersistentState() {
        constructor() : this("")
    }
}