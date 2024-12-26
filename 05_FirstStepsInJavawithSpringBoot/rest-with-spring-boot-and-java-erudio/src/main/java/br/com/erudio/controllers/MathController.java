package br.com.erudio.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.Converters.NumberConverter;
import br.com.erudio.excpetions.UnsupportedMathOperationException;
import br.com.erudio.math.SimpleMath;

@RestController
public class MathController 
{
	private SimpleMath math = new SimpleMath();
		
	@RequestMapping(value ="/sum/{numberOne}/{numberTwo}", method=RequestMethod.GET)
	public Double sum(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) throws Exception
	{
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) 
		{
			throw new UnsupportedMathOperationException("Please set a numeric value!");
		}
		
		return math.sum(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value ="/sub/{numberOne}/{numberTwo}", method=RequestMethod.GET)
	public Double subtraction(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) throws Exception
	{
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) 
		{
			throw new UnsupportedMathOperationException("Please set a numeric value!");
		}
		
		return math.subtraction(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value ="/mul/{numberOne}/{numberTwo}", method=RequestMethod.GET)
	public Double multiply(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) throws Exception
	{
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) 
		{
			throw new UnsupportedMathOperationException("Please set a numeric value!");
		}
		
		return math.multiply(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value ="/div/{numberOne}/{numberTwo}", method=RequestMethod.GET)
	public Double division(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) throws Exception
	{
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) 
		{
			throw new UnsupportedMathOperationException("Please set a numeric value!");
		}
		
		return math.division(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value ="/med/{numberOne}/{numberTwo}", method=RequestMethod.GET)
	public Double mean(@PathVariable(value = "numberOne") String numberOne, @PathVariable(value = "numberTwo") String numberTwo) throws Exception
	{
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) 
		{
			throw new UnsupportedMathOperationException("Please set a numeric value!");
		}
		
		return math.mean(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value ="/sqrt/{number}", method=RequestMethod.GET)
	public Double squareRoot(@PathVariable(value = "number") String number) throws Exception
	{
		if(!NumberConverter.isNumeric(number)) 
		{
			throw new UnsupportedMathOperationException("Please set a numeric value!");
		}
		
		return math.squareRoot(NumberConverter.convertToDouble(number));
	}
	
}
