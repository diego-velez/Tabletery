/**
 * Formats multiple tables according to liking.
 *
 * @param tablesHorizontally The number of tables to format horizontally. If this is greater than the amount of tables,
 * then it will format all the tables horizontally. This values must be greater than 0. Default is 1.
 * @param preserveOrder Preserves the order in which each table was added. If it is false, then it sorts the tables
 * displayed horizontally from the table with the most rows to the table with the least amount of rows. Default is true.
 * @param paddingBetweenTables Amount of padding in between each table horizontally, this only matters when
 * [tablesHorizontally] is greater than 1.
 * @throws IllegalArgumentException Thrown when [tablesHorizontally] is less than 0.
 * @throws NoSuchElementException Thrown when [tablesHorizontally] is 0.
 */
class TableFormatter(
    var tablesHorizontally: Int = 1,
    var preserveOrder: Boolean = true,
    var paddingBetweenTables: Int = 1
) {
    val tables = mutableListOf<Table>()

    /**
     * Formats all the tables, and returns the list.
     *
     * @return A list as a result of formatting all the tables appropriately, where each item represents
     * a line respectively.
     */
    fun toList(): List<String> {
        return toList(0, tablesHorizontally)
    }

    /**
     * Formats tables from [tableFirstIndex] (inclusive) to [tableLastIndex] (exclusive) horizontally.
     *
     * @param tableFirstIndex The index of the first table to format (inclusive).
     * @param tableLastIndex The index of the last table to format (exclusive).
     * @param tablePreviousWidth The total character width of the previous tables, excluding the padding.
     * @param tablePreviousPadding The padding applied to the previous tables.
     * @return A list of all the tables starting with [tableFirstIndex] to [tableLastIndex].
     * @throws IllegalArgumentException Thrown when [tablesHorizontally] is less than 0.
     * @throws NoSuchElementException Thrown when [tablesHorizontally] is 0.
     */
    private fun toList(
        tableFirstIndex: Int,
        tableLastIndex: Int,
        tablePreviousWidth: Int = 0,
        tablePreviousPadding: Int = 0
    ): List<String> {
        val tablesToAdd = if (tableLastIndex > tables.size) tables else tables.subList(tableFirstIndex, tableLastIndex)
        val rowsMaximum = tablesToAdd.maxOf { it.rows }
        val result = mutableListOf<String>()

        // Sorts the tables from the table with the most rows to the table with the least amount of rows.
        if (!preserveOrder) {
            tablesToAdd.sortByDescending { it.rows }
        }

        // Adds the first table
        result.addAll(tablesToAdd.first().toList())

        // Makes sure that all the rows are present in order to properly add later tables that are bigger
        // than the previous one.
        while (result.size < rowsMaximum) {
            result.add(" ".repeat(tablesToAdd.first().width))
        }

        // Adds the rest of the tables.
        for (rowIndex in 0 until rowsMaximum) {
            for (table in tablesToAdd.subList(1, tablesToAdd.size)) {
                /**
                 * @throws IndexOutOfBoundsException Thrown when this table has fewer rows that the table with the
                 * most amount of rows. When this happens, it will fill the rest of the rows with empty space
                 * in order for everything to be in order.
                 */
                try {
                    result[rowIndex] = result[rowIndex] + " ".repeat(paddingBetweenTables) + table.toList()[rowIndex]
                } catch (e: IndexOutOfBoundsException) {
                    result[rowIndex] = result[rowIndex] + " ".repeat(table.width + paddingBetweenTables)
                }
            }
        }

        // Calculates the padding needed to centralize the tables to the previous tables and applies it.
        val widthTotal = result.first().length
        var widthPadding = 0

        if (result.first().length < tablePreviousWidth) {
            widthPadding = ((tablePreviousWidth - result.first().length) / 2) + tablePreviousPadding
        } else if (result.first().length == tablePreviousWidth) {
            widthPadding = tablePreviousPadding
        }

        result.forEachIndexed { index, row ->
            result[index] = " ".repeat(widthPadding) + row
        }

        // Adds another batch of tables horizontally if it can, otherwise adds all the tables that are left (if any).
        if (tableLastIndex + tablesHorizontally <= tables.size) {
            val nextTableLastIndex = tableLastIndex + tablesHorizontally
            result.addAll(toList(tableLastIndex, nextTableLastIndex, widthTotal, widthPadding))
        } else if (tableLastIndex < tables.size) {
            val nextTableLastIndex = tableLastIndex + (tables.size - tableLastIndex)
            result.addAll(toList(tableLastIndex, nextTableLastIndex, widthTotal, widthPadding))
        }

        return result
    }

    /**
     * A string of all the tables formatted properly. This is what should be printed to the console.
     *
     * @return The string of all the tables formatted properly.
     */
    override fun toString(): String {
        return toList().joinToString("\n")
    }
}