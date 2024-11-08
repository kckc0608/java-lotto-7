package lotto;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.Randoms;
import lotto.constant.LottoResult;
import lotto.view.LottoView;

import java.util.*;

import static lotto.constant.ErrorMessage.*;

public class Application {
    private static List<Integer> getWinningNumbersWithInput() {
        System.out.println(MESSAGE_FOR_WINNING_NUMBERS_INPUT);
        while (true) {
            String winningNumbersInput = Console.readLine();
            try {
                validateWinningNumbersInput(winningNumbersInput);
            } catch (IllegalArgumentException e) {
                continue;
            }
            return Arrays.stream(winningNumbersInput.split(",")).map(Integer::parseInt).toList();
        }
    }

    private static void validateWinningNumbersInput(String winningNumbersInput) {
        List<String> winningNumbersStrings = Arrays.stream(winningNumbersInput.split(",")).toList();
        List<Integer> winningNumbers = new ArrayList<>();
        for (String numberString: winningNumbersStrings) {
            try {
                int number = Integer.parseInt(numberString);
                winningNumbers.add(number);
            } catch (NumberFormatException e) {
                printErrorMessage(ERROR_MESSAGE_FOR_WINNING_NUMBER_TYPE);
                throw new IllegalArgumentException();
            }
        }

        if (winningNumbers.size() != 6) {
            printErrorMessage(ERROR_MESSAGE_FOR_WINNING_NUMBER_COUNT);
            throw new IllegalArgumentException();
        }

        for (int number: winningNumbers) {
            if (number < 1 || number > 45) {
                printErrorMessage(ERROR_MESSAGE_FOR_WINNING_NUMBER_RANGE);
                throw new IllegalArgumentException();
            }
        }

        Set<Integer> winningNumberSet = Set.copyOf(winningNumbers);
        if (winningNumberSet.size() != 6) {
            printErrorMessage(ERROR_MESSAGE_FOR_WINNING_NUMBER_DUPLICATE);
            throw new IllegalArgumentException();
        }
    }

    private static int getBonusNumberWithInput() {
        System.out.println(MESSAGE_FOR_BONUS_NUMBERS_INPUT);
        while (true) {
            String bonusNumberInput = Console.readLine();
            try {
                validateBonusNumberInput(bonusNumberInput);
            } catch (IllegalArgumentException e) {
                continue;
            }
            return Integer.parseInt(bonusNumberInput);
        }
    }

    private static void validateBonusNumberInput(String bonusNumberInput) {
        int bonusNumber;
        try {
            bonusNumber = Integer.parseInt(bonusNumberInput);
        } catch (NumberFormatException e) {
            printErrorMessage(ERROR_MESSAGE_FOR_BONUS_NUMBER_TYPE);
            throw new IllegalArgumentException();
        }

        if (bonusNumber < 1 || bonusNumber > 45) {
            printErrorMessage(ERROR_MESSAGE_FOR_BONUS_NUMBER_RANGE);
            throw new IllegalArgumentException();
        }

        if (winningNumbers.contains(bonusNumber)) {
            printErrorMessage(ERROR_MESSAGE_FOR_BONUS_NUMBER_DUPLICATE);
            throw new IllegalArgumentException();
        }
    }

    private static void printErrorMessage(String errorMessage) {
        System.out.println(ERROR_MESSAGE_PREFIX + errorMessage);
    }

    private static final int LOTTO_PRICE = 1000;
    private static final String MESSAGE_FOR_PURCHASE_AMOUNT_INPUT = "구입금액을 입력해 주세요.";
    private static final String MESSAGE_FOR_WINNING_NUMBERS_INPUT = "당첨 번호를 입력해 주세요.";
    private static final String MESSAGE_FOR_BONUS_NUMBERS_INPUT = "보너스 번호를 입력해 주세요.";

    private static int purchaseAmount;
    private static int bonusNumber;
    private static List<Integer> winningNumbers;
    private static List<Lotto> lottos;

    public static void main(String[] args) {
        purchaseAmount = getPurchaseAmountWithInput();
        lottos = purchaseLottos(purchaseAmount);
        printPurchasedLottos();
        winningNumbers = getWinningNumbersWithInput();
        bonusNumber = getBonusNumberWithInput();

        LottoJudger judger = new LottoJudger(winningNumbers, bonusNumber);
        Map<LottoResult, Integer> statistics = judger.judge(lottos);
        LottoView.printLottoStatistics(statistics);
        LottoView.printLottoBenefitRate(statistics, purchaseAmount);
    }

    private static int getPurchaseAmountWithInput() {
        System.out.println(MESSAGE_FOR_PURCHASE_AMOUNT_INPUT);
        while (true) {
            String purchaseAmountInput = Console.readLine();
            try {
                validatePurchaseAmountInput(purchaseAmountInput);
            } catch (IllegalArgumentException e) {
                printErrorMessage(ERROR_MESSAGE_FOR_PURCHASE_AMOUNT_INPUT);
                continue;
            }
            return Integer.parseInt(purchaseAmountInput);
        }
    }

    private static void validatePurchaseAmountInput(String purchaseAmountInput) {
        try {
            int purchaseAmount = Integer.parseInt(purchaseAmountInput);
            if (purchaseAmount % 1000 != 0) {
                throw new IllegalArgumentException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }

    private static List<Lotto> purchaseLottos(int purchaseAmount) {
        List<Lotto> purchasedLottos = new ArrayList<>();
        while (purchaseAmount > 0) {
            List<Integer> numbers = Randoms.pickUniqueNumbersInRange(1, 45, 6);
            Lotto lotto;
            try {
                lotto = new Lotto(numbers);
            } catch (IllegalArgumentException e) {
                continue;
            }
            purchasedLottos.add(lotto);
            purchaseAmount -= LOTTO_PRICE;
        }
        return purchasedLottos;
    }

    private static void printPurchasedLottos() {
        System.out.println("\n" + lottos.size() + "개를 구매했습니다.");
        for (Lotto lotto : lottos) {
            lotto.printLottoNumbers();
        }
        System.out.println();
    }
}
