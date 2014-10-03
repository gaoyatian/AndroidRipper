package it.unina.android.ripper.planner.widget_inputs.values_generator;

public class RandomNumericValuesGenerator implements ValuesGenerator
{

	int Min = 0;
	int Max = 0;
	
	public RandomNumericValuesGenerator(int max) {
		this.Max = max;
	}
	
	public RandomNumericValuesGenerator(int min, int max)
	{
		this.Min = min;
		this.Max = max;
	}

	@Override
	public String generate() {
		int randomInt = Min + (int)(Math.random() * ((Max - Min) + 1));
		return Integer.toString(randomInt);
	}

}
