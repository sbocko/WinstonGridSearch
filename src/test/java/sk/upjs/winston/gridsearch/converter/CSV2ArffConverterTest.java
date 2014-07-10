package sk.upjs.winston.gridsearch.converter;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

public class CSV2ArffConverterTest extends TestCase {

    @Test
    public void testConvertCsvToArff() throws Exception {
        CSV2ArffConverter converter = new CSV2ArffConverter();
        File outputArff = new File("other/dataset.arff");
        assertTrue(converter.convertCsvToArff(new File("other/dataset.csv"), outputArff));

        outputArff.delete();
    }

    @Test
    public void testConvertCsvToArffWrongCsv() throws Exception {
        CSV2ArffConverter converter = new CSV2ArffConverter();
        File outputArff = new File("other/wrongCsv.arff");
        assertFalse(converter.convertCsvToArff(new File("other/wrongCsv.csv"), outputArff));
    }

    @Test
    public void testConvertCsvToArffWrongCsvFilePath() throws Exception {
        CSV2ArffConverter converter = new CSV2ArffConverter();
        File outputArff = new File("other/output.arff");
        assertFalse(converter.convertCsvToArff(new File("/this/is/wrong/path/to/csv/file"), outputArff));
    }

    @Test
    public void testConvertCsvToArffWrongArffFilePath() throws Exception {
        CSV2ArffConverter converter = new CSV2ArffConverter();
        File outputArff = new File("/blah/blah/dataset.arff");
        assertFalse(converter.convertCsvToArff(new File("other/dataset.csv"), outputArff));
    }
}