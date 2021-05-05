package UserModel;

public class UserModelConst {
    static final String Setup = "Setup";
    static final String Food = "Food";
    static final String ConfirmMenu = "ConfirmMenu";
    static final double NormalCost = 500.0;
    static final double ReducedCost = 400.0;

    public static class AnswerType{
        public static final int Yes = 1;
        public static final int No = 0;
        public static final int Add = 2;
        public static final int Other = 3;
        public static final int Reduce = 4;
        public static final int EnergyInfo = 5;
        public static final int CostInfo = 6;
        public static final int NutritionalBalanceInfo = 7;
        public static final int Info = 8;
    }
}
