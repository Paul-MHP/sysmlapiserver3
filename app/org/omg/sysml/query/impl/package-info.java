/*
 * SysML v2 REST/HTTP Pilot Implementation
 * Copyright (C) 2020  InterCAX LLC
 * Copyright (C) 2020  California Institute of Technology ("Caltech")
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * @license LGPL-3.0-or-later <http://spdx.org/licenses/LGPL-3.0-or-later>
 */

@AnyMetaDefs(value = {
        @AnyMetaDef(name = "ConstraintMetaDef", metaType = "string", idType = "java.util.UUID",
                metaValues = {
                        @MetaValue(value = "CompositeConstraint", targetEntity = CompositeConstraintImpl.class),
                        @MetaValue(value = "PrimitiveConstraint", targetEntity = PrimitiveConstraintImpl.class),
                }),
        @AnyMetaDef(name = "QueryMetaDef", metaType = "string", idType = "java.util.UUID",
                metaValues = {
                        @MetaValue(value = "Query", targetEntity = QueryImpl.class),
                }),
})

@GenericGenerators(value = {
        @GenericGenerator(name = "UseExistingOrGenerateUUIDGenerator", strategy = "jpa.UseExistingOrGenerateUUIDGenerator")
})
package org.omg.sysml.query.impl;

import org.hibernate.annotations.*;