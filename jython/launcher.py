import sys
sys.path.append('lib/SD_Translator.jar')
import argparse
from drivers import Tester
import lib.PapyrusToXML as PapyrusToXML


def main(policy_file, regulation_file):

    print "Processing Papyrus XML file..."
    # call the PapyrusToXML to generate intermediate.xml file
    PapyrusToXML.main(policy_file, "policy.xml")
    PapyrusToXML.main(regulation_file, "regulation.xml")

    print "Generating LTL..."
    Tester.conformanceToFile("policy.xml", "regulation.xml")

    print "Complete."
if __name__ == "__main__":
    # parse the command line arguments
    parser = argparse.ArgumentParser(description='Papyrus XML converter/SDAF')
    parser.add_argument('-i', '--input',
                        help='input file name', required=True)
    parser.add_argument('-i2', '--input2',
                        help='input file name', required=True)
    args = vars(parser.parse_args())

    # pass input file names to main
    main(args['input'], args['input2'])
