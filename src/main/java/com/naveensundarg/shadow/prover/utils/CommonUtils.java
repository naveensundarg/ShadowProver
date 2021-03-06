package com.naveensundarg.shadow.prover.utils;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.naveensundarg.shadow.prover.utils.Reader.extractForms;
import static com.naveensundarg.shadow.prover.utils.Reader.readFormula;
import static us.bpsm.edn.parser.Parsers.defaultConfiguration;

/**
 * Created by naveensundarg on 4/8/16.
 */
public class CommonUtils {

    public static String toString(Object[] objects) {

        StringBuilder stringBuilder = new StringBuilder();


        for (Object object : objects) {
            stringBuilder.append(object);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }


    public static List<Pair<Set<Formula>, Formula>> readCases(InputStream testFile) throws Reader.ParsingException {

        List<Pair<Set<Formula>, Formula>> cases = new ArrayList<>();
        Scanner scanner = new Scanner(testFile);



        while (scanner.hasNext()) {

            String line = scanner.nextLine();

            while (line != null) {
                if (line.startsWith("\"begin")) {

                    Set<Formula> assumptions = Sets.newSet();
                    Formula formula = null;
                    line = scanner.nextLine();
                    while (!line.startsWith("\"end")) {

                        List<String> parts = extractForms(line);
                        if (parts.get(0).startsWith(" \"assumption")) {


                            Parseable pbr = Parsers.newParseable(parts.get(1));
                            Parser p = Parsers.newParser(defaultConfiguration());


                            assumptions.add(readFormula(p.nextValue(pbr)));
                        }

                        if (parts.get(0).equals(" \"goal\" ")) {

                            Parseable pbr = Parsers.newParseable(parts.get(1));
                            Parser p = Parsers.newParser(defaultConfiguration());

                            formula = (readFormula(p.nextValue(pbr)));
                        }


                        line = scanner.nextLine();
                    }

                    cases.add(ImmutablePair.from(assumptions, formula));

                }

                if (scanner.hasNext())
                    line = scanner.nextLine();
                else
                    break;
                ;
            }


        }

        return cases;


    }

    public static int maxLevel(Formula[] formulas) {

        OptionalInt optionalInt = Arrays.stream(formulas).mapToInt(Formula::getLevel).max();

        return optionalInt.orElseGet(() -> 0);
    }

    public static <T> List<List<T>> setPower(Set<T> objects, int n) {

        if (n <= 0) {
            return CollectionUtils.newEmptyList();

        }

        if(n == 1){

            return objects.stream().map(CollectionUtils::listOf).collect(Collectors.toList());


        } else {

            List<List<T>> smallerTuples = setPower(objects, n-1);

            return smallerTuples.stream().flatMap(tList->objects.stream().map(object-> CollectionUtils.addToList(tList,object))).collect(Collectors.toList());
        }

    }

    public static void main(String[] args){

        Set<String> strings = new HashSet<>();
        strings.add("a");
        strings.add("b");
        strings.add("c");

        setPower(strings,3).stream().forEach(System.out::println);
    }

}
