/**
 * Represents a table where each data in a "cell" is center aligned.
 * The table can have a title, a header and a footer.
 * Each row must have the same amount of columns.
 *
 * @param data The main data that the table will display. This cannot be null.
 * @param header The row that appears on top of the data. Typically, it provides the name of each column respectively.
 * @param footer The row that appears at the bottom of the data. Typically, it provides the totals of the data.
 * @param title The title of the table, appears at the top.
 */
class Table private constructor(
    private val data: List<List<String>>,
    private val header: List<String>? = null,
    private val footer: List<String>? = null,
    private val title: String? = null
) {

    /**
     * Amount of rows in the table.
     */
    val rows: Int
        get() {
            // Account for the data and its two division lines
            var result = data.size + 2

            // Account for the title and its division line
            if (title != null) {
                result += 2;
            }

            // Account for the header and its division line
            if (header != null) {
                result += 2;
            }

            // Account for the footer and its division line
            if (footer != null) {
                result += 2;
            }

            return result
        }

    /**
     * Amount of columns in the table.
     */
    val columns = data.first().size

    /**
     * Amount of characters in the table horizontally.
     */
    val width: Int
        get() = getColumnsWidthMax().sum() + (3 * (columns - 1)) + 4

    /**
     * Builder class for a [Table] object.
     */
    class TableBuilder {
        private var header: List<String>? = null
        private var data: List<List<String>>? = null
        private var footer: List<String>? = null
        private var title: String? = null

        /**
         * Adds a header to the table.
         *
         * @param header A list where each item is put in a column respectively.
         * @return This [TableBuilder] object.
         */
        fun addHeader(header: List<String>): TableBuilder {
            this.header = header
            return this
        }

        /**
         * Adds the data to the table.
         *
         * @param data A list of lists where each list represents a row in the table, and each item is the data put
         * in a respective column of the table.
         * @return This [TableBuilder] object.
         */
        fun addData(data: List<List<String>>): TableBuilder {
            this.data = data
            return this
        }

        /**
         * Adds a footer to the table.
         *
         * @param footer A list where each item is put in a column respectively.
         * @return This [TableBuilder] object.
         */
        fun addFooter(footer: List<String>): TableBuilder {
            this.footer = footer
            return this
        }

        /**
         * Adds a title to the table.
         *
         * @param title The title of the table.
         * @return This [TableBuilder] object.
         */
        fun addTitle(title: String): TableBuilder {
            this.title = title
            return this
        }

        @Throws(IllegalStateException::class)
        /**
         * Builds the table.
         *
         * @return The table object based on this [TableBuilder].
         * @throws IllegalStateException Thrown if there was no data passed or if the data is null.
         */
        fun build(): Table {
            if (data == null) {
                throw IllegalStateException("You must add data to the Table using addData")
            }
            checkColumnAmount()

            return Table(data!!, header, footer, title)
        }

        @Throws(AssertionError::class)
        /**
         * Makes sure that the data, header and footer all have the same amount of columns.
         *
         * @throws AssertionError Thrown if the amount of columns varies.
         */
        private fun checkColumnAmount() {
            val rows = mutableListOf<List<String>>()

            if (header != null) {
                rows.add(header!!)
            }

            rows.addAll(data!!)

            if (footer != null) {
                rows.add(footer!!)
            }

            val rowMaxColumns = rows.maxBy { it.size }
            rows
                .forEach { row ->
                    assert(row.size == rowMaxColumns.size) {
                        "Row with first element of ${row.first()} does not have the same amount of" +
                            "columns as row with first element of ${rowMaxColumns.first()}"
                    }
                }
        }
    }

    /**
     * Gets all the raw rows in the table, meaning the header, data and footer. Does not include the title.
     *
     * @return A list of a list of the data in one row of the table. Each inner list represents a row in the table, and
     * each item represents the respective data in a column.
     */
    fun getRows(): List<List<String>> {
        val rows = mutableListOf<List<String>>()

        if (header != null) {
            rows.add(header)
        }

        rows.addAll(data)

        if (footer != null) {
            rows.add(footer)
        }

        return rows
    }

    /**
     * Formats the table properly.
     *
     * @return The table properly formatted. This is what should be printed out to the console.
     */
    override fun toString(): String {
        return toList().joinToString("\n")
    }

    /**
     * Formats the table properly.
     *
     * @return The table properly formatted, where each item is a single row. This joined to string with a newline as
     * separator is what should be printed to the console.
     */
    fun toList(): List<String> {
        val spaces = getColumnsWidthMax()
        val result = mutableListOf<String>()

        // Add the title
        if (title != null) {
            result.add("-".repeat(width))
            result.add("|" + formatColumn(title, width - 2) + "|")
        }

        // Add the header
        if (header != null) {
            result.add("-".repeat(width))
            result.add(createRow(header, spaces))
        }

        // Add the data
        result.add("-".repeat(width))
        data.forEach { row ->
            result.add(createRow(row, spaces))
        }
        result.add("-".repeat(width))

        // Add the footer
        if (footer != null) {
            result.add(createRow(footer, spaces))
            result.add("-".repeat(width))
        }

        return result
    }

    /**
     * Gets the maximum amount of characters allowed per column respectively.
     *
     * @return A list where each number represents the maximum amount of characters allowed per column respectively.
     */
    private fun getColumnsWidthMax(): List<Int> {
        val columnsCharacterMax = mutableListOf<Int>()

        val rows = getRows()
        for (index in 0 until columns) {
            columnsCharacterMax.add(rows.maxOf { it[index].length })
        }

        return columnsCharacterMax
    }

    /**
     * Properly formats a single row based on the data to add and maximum amount of spaces of each column.
     *
     * @param row A list where each item within is put in a single column respectively.
     * @param widthMaximums A list where each number in the list represents the maximum amount of
     * spaces for each column respectively.
     * @return Returns a single row of the table based on the data passed.
     */
    private fun createRow(row: List<String>, widthMaximums: List<Int>): String {
        val result = StringBuilder()
        result.append("| ")

        for (pair in row zip widthMaximums) {
            // Only add the ' | ' for all items except the first one.
            if (row.first() != pair.first) {
                result.append(" | ")
            }
            result.append(formatColumn(pair.first, pair.second))
        }

        result.append(" |")
        return result.toString()
    }

    /**
     * Formats a single column properly with the [data] to put in it and the [widthMax].
     *
     * @param data Data to add into the cell.
     * @param widthMax The maximum amount of characters allowed into that column. This number must be greater than
     * or equal to the length of the [data] string.
     * @return Returns a single cell properly formatted based on the data passed.
     */
    private fun formatColumn(data: String, widthMax: Int): String {
        val spacesLeft = widthMax - data.length

        val column = StringBuilder()
        column.append(" ".repeat(spacesLeft / 2))
        column.append(data)
        column.append(" ".repeat(spacesLeft / 2))

        // Whenever there is a word with odd character length then it will be missing spaces
        if (column.length < widthMax) {
            column.append(" ".repeat(widthMax - column.length))
        }

        return column.toString()
    }
}