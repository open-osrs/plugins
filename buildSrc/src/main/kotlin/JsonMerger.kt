/**
 * Copyright 2018 Savvas Dalkitsis
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import JsonMerger.ArrayMergeMode
import JsonMerger.ArrayMergeMode.MERGE_ARRAY
import JsonMerger.ArrayMergeMode.REPLACE_ARRAY
import JsonMerger.ObjectMergeMode
import JsonMerger.ObjectMergeMode.MERGE_OBJECT
import JsonMerger.ObjectMergeMode.REPLACE_OBJECT
import org.json.JSONArray
import org.json.JSONObject

private const val OVERRIDE_OBJECT_MERGE_KEY = "__json-merge:objectMergeMode"

/**
 * Main object used to perform json merges. Accepts two parameters to control
 * how the merge will be performed.
 *
 * @param arrayMergeMode This controls how arrays will be handled. Arrays can
 * either be merged or replaced (default mode). See [ArrayMergeMode] for more
 * details
 * @param objectMergeMode This controls how objects will be handled. Objects can
 * either be merged (default mode) or replaced. See [ObjectMergeMode] for more
 * details
 */
class JsonMerger @JvmOverloads constructor(val arrayMergeMode: ArrayMergeMode = REPLACE_ARRAY,
                                           val objectMergeMode: ObjectMergeMode = MERGE_OBJECT) {

    /**
     * Controls how arrays are handled. [REPLACE_ARRAY] will always use
     * the array from the override json, replacing the one in base and
     * [MERGE_ARRAY] will simply append the items from the array in the
     * override json into the base array (if present. if not, only items
     * from the override array will be used)
     */
    enum class ArrayMergeMode {
        REPLACE_ARRAY,
        MERGE_ARRAY
    }

    /**
     * Controls how objects are handled. [REPLACE_OBJECT] will always use
     * the object from the override json, replacing the one in base and
     * [MERGE_OBJECT] will merge the two by using all items in the base
     * object that are not present in the override and for the common items
     * use the ones from the override (recursively applying the same rules
     * for sub objects and arrays)
     */
    enum class ObjectMergeMode {
        REPLACE_OBJECT,
        MERGE_OBJECT
    }

    /**
     * Parse and merge the provided json strings using the rules applied
     * via [ArrayMergeMode] and [ObjectMergeMode]. This is a **recursive**
     * call.
     *
     * @return the merged json as a [String]
     */
    fun merge(baseJson: String, overrideJson: String): String = try {
        mergeElement(parse(baseJson), parse(overrideJson)).toString()
    } catch (e: Exception) {
        throw throwUnsupportedElements(baseJson, overrideJson, e)
    }

    /**
     * Merges the provided [JSONObject]s using the rules applied via
     * [ArrayMergeMode] and [ObjectMergeMode]. This is a **recursive**
     * call.
     *
     * @return the merged json as a [JSONObject]
     */
    fun merge(baseJson: JSONObject, overrideJson: JSONObject): JSONObject = mergeElement(baseJson, overrideJson) as JSONObject

    /**
     * Merges the provided [JSONObject]s using the rules applied via
     * [ArrayMergeMode] and [ObjectMergeMode]. This is a **recursive**
     * call.
     *
     * @return the merged json as a [String]
     */
    fun mergeToString(baseJson: JSONObject, overrideJson: JSONObject): String = merge(baseJson, overrideJson).toString()

    private fun mergeElement(base: Any?, newValue: Any, objectMergeMode: ObjectMergeMode = this.objectMergeMode) = when (base) {
        is JSONObject -> mergeObject(base, newValue, objectMergeMode)
        is JSONArray -> mergeArray(base, newValue)
        else -> newValue
    }

    private fun maybeOverrideMergeMode(override: Any, objectMergeMode: ObjectMergeMode): ObjectMergeMode {
        return when ((override as? JSONObject)?.valueOrNull(OVERRIDE_OBJECT_MERGE_KEY)) {
            "replaceObject" -> {
                override.remove(OVERRIDE_OBJECT_MERGE_KEY)
                REPLACE_OBJECT
            }
            "mergeObject" -> {
                override.remove(OVERRIDE_OBJECT_MERGE_KEY)
                MERGE_OBJECT
            }
            else -> objectMergeMode
        }
    }

    private fun mergeObject(base: JSONObject, override: Any, objectMergeMode: ObjectMergeMode): JSONObject {
        if (override !is JSONObject) {
            throw IllegalArgumentException(msg(base, override))
        }
        override.keys().forEach { key ->
            val mergeMode = maybeOverrideMergeMode(override[key], objectMergeMode)
            base[key] = when (mergeMode) {
                REPLACE_OBJECT -> override[key]
                MERGE_OBJECT -> mergeElement(base.valueOrNull(key), override[key], mergeMode)
            }
        }
        return base
    }

    private fun mergeArray(base: JSONArray, override: Any): JSONArray {
        if (override !is JSONArray) {
            throw IllegalArgumentException(msg(base, override))
        }
        return when (arrayMergeMode) {
            REPLACE_ARRAY -> override
            MERGE_ARRAY -> base.apply { override.forEach { put(it) } }
        }
    }

    private fun parse(json: String) = try {
        JSONObject(json)
    } catch (_: Throwable) {
        JSONArray(json)
    }

    private operator fun JSONObject.set(key: String, value: Any) {
        put(key, value)
    }

    private fun msg(base: Any, override: Any) =
            "Trying to merge two elements of different type. Base type was ${base::class} and override was ${override::class}"

    private fun throwUnsupportedElements(baseJson: String, overrideJson: String, cause: Exception) =
            IllegalArgumentException("Can only merge json objects or arrays. Base json was '$baseJson', override was '$overrideJson'", cause)

}

/**
 * Merges two [JSONObject]s using a [JsonMerger] with the default merge modes
 */
infix fun JSONObject.mergeWith(overrideJson: JSONObject) = JsonMerger().merge(baseJson = this, overrideJson = overrideJson)

private fun JSONObject.valueOrNull(key: String) = if (has(key)) this[key] else null