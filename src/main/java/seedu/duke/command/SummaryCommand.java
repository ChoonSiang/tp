package seedu.duke.command;

import seedu.duke.classes.Expense;
import seedu.duke.classes.Income;
import seedu.duke.classes.StateManager;
import seedu.duke.exception.DukeException;
import seedu.duke.ui.Ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;

public class SummaryCommand extends Command {

    private static final String TYPE_ARG = "type";
    private static final String TYPE_IN = "in";
    private static final String TYPE_OUT = "out";
    private static final String MISSING_TYPE = "Please indicate the transaction type.";
    private static final String INVALID_TYPE = "Please indicate either /type in or /type out.";
    private static final String EMPTY_LIST = "It appears that we have come up empty. Why not try adding some" +
            " transactions first?";
    private static final String STARTING_INCOME_MSG = "Good job! Total income";
    private static final String STARTING_EXPENSE_MSG = "Wise spending! Total expense";
    private static final String DAY_ARG = "day";
    private static final String WEEK_ARG = "week";
    private static final String MONTH_ARG = "month";

    private boolean filterByDay = false;
    private boolean filterByWeek = false;
    private boolean filterByMonth = false;
    private Ui ui;

    public SummaryCommand(String description, HashMap<String, String> args) {
        super(description, args);
    }

    @Override
    public void execute(Ui ui) throws DukeException {
        this.ui = ui;
        throwIfInvalidDescOrArgs();
        getFilter();
        printSummary();
    }

    private void throwIfInvalidDescOrArgs() throws DukeException {
        assert getArgs() != null;

        String typeArg = getArg(TYPE_ARG);
        if (typeArg == null) {
            throw new DukeException(MISSING_TYPE);
        }

        if (!(typeArg.equalsIgnoreCase(TYPE_IN) || typeArg.equalsIgnoreCase(TYPE_OUT))) {
            throw new DukeException(INVALID_TYPE);
        }
    }

    private void getFilter() {
        if (getArgs().containsKey(DAY_ARG)) {
            filterByDay = true;
        } else if (getArgs().containsKey(WEEK_ARG)) {
            filterByWeek = true;
        } else if (getArgs().containsKey(MONTH_ARG)) {
            filterByMonth = true;
        }
    }

    private double getIncomeSummary() throws DukeException {
        ArrayList<Income> incomeArray = StateManager.getStateManager().sortedIncomes();
        if (incomeArray == null || incomeArray.isEmpty()) {
            throw new DukeException(EMPTY_LIST);
        }
        if (filterByDay || filterByWeek || filterByMonth) {
            incomeArray = filterIncome(incomeArray);
        }
        double totalSum = 0;
        for (Income income : incomeArray) {
            totalSum = totalSum + income.getTransaction().getAmount();
        }

        return totalSum;
    }

    private ArrayList<Income> filterIncome(ArrayList<Income> transactionsArrayList) {
        ArrayList<Income> filteredArrayList = new ArrayList<>();
        for (Income transaction : transactionsArrayList) {
            LocalDate transactionDate = transaction.getTransaction().getDate();
            if (filterByDay && isSameDay(transactionDate)) {
                filteredArrayList.add(transaction);
            } else if (filterByWeek && isSameWeek(transactionDate)) {
                filteredArrayList.add(transaction);
            } else if (filterByMonth && isSameMonth(transactionDate)) {
                filteredArrayList.add(transaction);
            }
        }
        return filteredArrayList;
    }

    private double getExpenseSummary() throws DukeException {
        ArrayList<Expense> expenseArray = StateManager.getStateManager().sortedExpenses();
        if (expenseArray == null || expenseArray.isEmpty()) {
            throw new DukeException(EMPTY_LIST);
        }
        if (filterByDay || filterByWeek || filterByMonth) {
            expenseArray = filterExpense(expenseArray);
        }
        double totalSum = 0;
        for (Expense expense : expenseArray) {
            totalSum = totalSum + expense.getTransaction().getAmount();
        }

        return totalSum;
    }

    private ArrayList<Expense> filterExpense(ArrayList<Expense> transactionsArrayList) {
        ArrayList<Expense> filteredArrayList = new ArrayList<>();
        for (Expense transaction : transactionsArrayList) {
            LocalDate transactionDate = transaction.getTransaction().getDate();
            if (filterByDay && isSameDay(transactionDate)) {
                filteredArrayList.add(transaction);
            } else if (filterByWeek && isSameWeek(transactionDate)) {
                filteredArrayList.add(transaction);
            } else if (filterByMonth && isSameMonth(transactionDate)) {
                filteredArrayList.add(transaction);
            }
        }
        return filteredArrayList;
    }

    private boolean isSameDay(LocalDate transactionDate) {
        LocalDate currentDate = LocalDate.now();
        return currentDate.isEqual(transactionDate);
    }

    private boolean isSameWeek(LocalDate transactionDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        if (transactionDate.isBefore(startOfWeek) || transactionDate.isAfter(endOfWeek)) {
            return false;
        }
        return true;
    }

    private boolean isSameMonth(LocalDate transactionDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfMonth = currentDate.withDayOfMonth(1);
        LocalDate endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        if (transactionDate.isBefore(startOfMonth) || transactionDate.isAfter(endOfMonth)) {
            return false;
        }
        return true;
    }

    private void printSummary() throws DukeException {
        String msg = "";
        if (getArg(TYPE_ARG).equals(TYPE_IN)) {
            double totalSum = getIncomeSummary();
            msg = getSummaryMsg(TYPE_IN, totalSum);
        } else if (getArg(TYPE_ARG).equals(TYPE_OUT)) {
            double totalSum = getExpenseSummary();
            msg = getSummaryMsg(TYPE_OUT, totalSum);
        }
        ui.print(msg);
    }

    private String getSummaryMsg(String type, double totalSum) {
        String msg = "";
        if (type.equals(TYPE_IN)) {
            msg = STARTING_INCOME_MSG;
        } else {
            msg = STARTING_EXPENSE_MSG;
        }
        if (filterByDay) {
            msg = msg + " for Today: $";
        } else if (filterByWeek) {
            msg = msg + " for This Week: $";
        } else if (filterByMonth) {
            msg = msg + " for This Month: $";
        } else {
            msg = msg + ": $";
        }
        return msg + ui.formatAmount(totalSum);
    }
}
