package com.javaman.util;

import java.math.BigDecimal;

public class Calculate {


	// Briz perde hesaplama
	public CalculationResult calcBriz(double brizWidth, double pile, double unitPrice) {

		return calcGeneralNetCurt(brizWidth, pile, unitPrice);

	}

	// Kruvaze Tül hesaplama
	public CalculationResult calcDoubleNetCurt(double width, double pile, double unitPrice) {

		CalculationResult result = new CalculationResult();

		double widthLast = width / 100;

		if (widthLast > 0 && widthLast < 2.5) {

			double lineAmount = (widthLast * pile + 2) * unitPrice;
			double usedMaterial = widthLast * pile + 2;
			result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
			result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));
			return result;

		} else if (widthLast >= 2.5 && widthLast <= 3.5) {

			double lineAmount = (widthLast * pile + 2.5) * unitPrice;
			double usedMaterial = widthLast * pile + 2.5;
			result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
			result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));
			return result;

		} else if (widthLast > 3.5 && widthLast <= 5) {

			double lineAmount = (widthLast * pile + 3.5) * unitPrice;
			double usedMaterial = widthLast * pile + 3.5;
			result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
			result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));
			return result;

		} else {
			double lineAmount = (widthLast * pile + 4) * unitPrice;
			double usedMaterial = widthLast * pile + 4;
			result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
			result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));
			return result;
		}

	}

	// Fon kanat hesaplama
	public CalculationResult calcFonKanat(double width, double pile, double unitPrice) {

		CalculationResult result = new CalculationResult();

		double widthLast = width / 100;
		double lineAmount = ((widthLast * pile) + 0.25) * unitPrice;
		double usedMaterial = ((widthLast * pile) + 0.25);

		result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
		result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));

		return result;
	}

	// Japon panel hesaplama
	public CalculationResult calcJapanPanel(double width, double unitPrice) {

		CalculationResult result = new CalculationResult();

		double widthLast = width / 100;
		double lineAmount = (widthLast * 2.5) * unitPrice;
		double usedMaterial = (widthLast * 2.5);

		result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
		result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));

		return result;

	}

	// Kruvaze fon hesaplama
	public CalculationResult calcKruvazeFon(double width, double pile, double unitPrice) {

		return calcGeneralNetCurt(width, pile, unitPrice);
	}

	private CalculationResult calcGeneralNetCurt(double width, double pile, double unitPrice) {
		CalculationResult result = new CalculationResult();

		double widthLast = width / 100;
		double lineAmount = widthLast * pile * unitPrice;
		double usedMaterial = widthLast * pile;


		result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
		result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));

		return result;
	}

	// Tül perde hesaplama
	public CalculationResult calcNetCurt(double width, double pile, double unitPrice) {

		return calcGeneralNetCurt(width, pile, unitPrice);
	}

	// stor ,zebra, dikey , tül stor ,jaluzi hesaplama
	public CalculationResult calcRollerZebraVerticalBlind(double width, double height, double unitPrice) {

		CalculationResult result = new CalculationResult();

		double widthLast = calcRollerZebraVerticalBlindWidth(width);
		double heightLast = calcRollerZebraVerticalBlindHeight(height);
		double lineAmount = widthLast * heightLast * unitPrice;
		double usedMaterial = widthLast * heightLast;
		result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
		result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));

		return result;
	}

	// stor ,zebra, dikey ,jaluzi ,tül stor hesaplama
	public double calcRollerZebraVerticalBlindHeight(double height) {

		if (height <= 200) {
			height = 200;
		} else if (height <= 260 && height > 200) {
			height = 260;
		} else if (height <= 300 && height > 260) {
			height = 300;
		}

		return height / 100;
	}

	// stor ,zebra, dikey , tül stor ,jaluzi hesaplama
	public double calcRollerZebraVerticalBlindWidth(double width) {
		if (width <= 100) {
			width = 100;

		} else {
			if (width % 10 != 0) {
				width = Math.round((width + 5) / 10) * 10;
			}
		}
		return width / 100;
	}

	// güneşlik hesaplama
	public CalculationResult calcSunBlind(double width, double unitPrice) {
		CalculationResult result = new CalculationResult();

		double widthLast = (width + 20) / 100;
		double lineAmount = widthLast * unitPrice;
		double usedMaterial = widthLast;

		result.setLineAmount(new BigDecimal(lineAmount).setScale(2,BigDecimal.ROUND_HALF_UP));
		result.setUsedMaterial(new BigDecimal(usedMaterial).setScale(2,BigDecimal.ROUND_HALF_UP));

		return result;
	}

}
