#!/usr/bin/env python

""" PapyrusToXML.py: converts Papyrus XML output to input for other tools """

""" The following must hold in order for the output of this program to work
    with Hui's tool:
        - no spaces in anything (labels, lifelines, messages, conditions etc)
        - all names must be unique
        """

import argparse
import re
import xml.dom.minidom
import xml.etree.ElementTree as ET

namespace = '{http://www.omg.org/spec/XMI/20110701}'  # annoying workaround


def handle_message(message, oldroot, newroot):
    """
    Returns nothing.

    Pass in a message element from Papyrus, along with the root of Papyrus
    and root of new XML document.  A message has two IDs, one for receiving
    lifeline and another for the sending lifeline.  Find those elements in
    the Papyrus XML, get their names, and backtrack in the new XML to find
    the lifeline elements that match that name.  Add the message to the new
    element in Hui's format.  Restriction: all lifeline names must be unique.
    """
    recv_id = message.attrib.get('receiveEvent')
    send_id = message.attrib.get('sendEvent')
    recv_lifeline_name = find_lifeline_by_cbid(oldroot, recv_id).attrib['name']
    send_lifeline_name = find_lifeline_by_cbid(oldroot, send_id).attrib['name']
    send_lifeline_elem = find_lifeline_by_name(newroot, send_lifeline_name)

    messageEvent = ET.SubElement(send_lifeline_elem, 'messageEvent')

    name = ET.SubElement(messageEvent, 'name')
    name.text = message.attrib.get('name').split(':')[1]  # name is 2nd item

    number = ET.SubElement(messageEvent, 'number')
    number.text = message.attrib.get('name').split(':')[0]  # number is 1st

    receiver = ET.SubElement(messageEvent, 'receiver')
    receiver.text = recv_lifeline_name

    msg_type = ET.SubElement(messageEvent, 'type')
    msg_type.text = message.attrib.get('messageSort', 'synchCall')


def find_lifeline_by_cbid(oldroot, cbid):
    """
    Returns a lifeline XML element.

    Look for a lifeline element in the Papyrus XML document that contains
    the specified id in the coveredBy attribute of the lifeline tag.
    """
    for lifeline in oldroot.findall('lifeline'):
        if cbid in lifeline.attrib.get('coveredBy').split():
            return lifeline


def find_lifeline_by_id(oldroot, lifeline_id):
    """
    Returns a lifeline XML element.

    Look for a lifeline element in the Papyrus XML document that contains
    the specified id in the xmi:id attribute of the lifeline tag.
    """
    for lifeline in oldroot.findall('lifeline'):
        if lifeline_id == lifeline.attrib.get(namespace + 'id'):
            return lifeline


def find_lifeline_by_name(newroot, name):
    """
    Returns a lifeline XML element.

    Look for (backtrack) an element that has the specified name in the new XML
    document.  Will return the first result.  Does not function correctly if
    more than one lifeline has the same name.
    """
    for lifeline in newroot.findall('lifeline'):
        if name == lifeline.find('roleName').text:
            return lifeline


def handle_lifeline(old_lifeline, sequenceDiagram):
    """
    Returns nothing.

    Add a lifeline element to the new XML document based on the Papyrus XML.
    """
    lifeline = ET.SubElement(sequenceDiagram, 'lifeline')
    roleName = ET.SubElement(lifeline, 'roleName')
    roleName.text = old_lifeline.attrib.get('name')
    type_tag = ET.SubElement(lifeline, 'type')
    type_tag.text = 'System'


def find_message_number_by_id(oldroot, message_id):
    """
    Returns a message number.

    Search Papyrus XML document for messages that match passed id.
    """
    for message in oldroot.findall('message'):
        if message.attrib[namespace + 'id'] == message_id:
            return message.attrib.get('name').split(':')[0]


def get_message_numbers_by_operand(oldroot, operand):
    """
    Returns a set of message numbers.

    Pass an operand XML element and find all messages corresponding to that
    element.
    """
    message_numbers = set()
    msg_tag_txt = 'uml:MessageOccurrenceSpecification'
    for fragment in operand.findall('fragment'):
        if fragment.attrib[namespace + 'type'] == msg_tag_txt:
            msg_id = fragment.attrib['message']
            msg_num = find_message_number_by_id(oldroot, msg_id)
            message_numbers.add(msg_num)
    return message_numbers


def choose_lifeline(lifeline_choices, operand_name):
    """
    Returns a lifeline name (string).

    given a list of lifeline names and a name of an operand, this function will
    ask the user to choose a single lifeline from this list.
    """
    print '\n\tChoose from the following list which lifeline "{}" belongs to:'\
        .format(operand_name)

    for ndx, choice in enumerate(lifeline_choices):
        print '\t\t', ndx, ':\t', choice

    user_choice = int(raw_input('\t\t---> '))

    try:
        return lifeline_choices[user_choice]
    except IndexError:
        return lifeline_choices[0]


def handle_combined_fragment(fragment, oldroot, sequenceDiagram):
    """
    Returns nothing.

    Add combined fragment element to the new XML document based on Papyrus XML.
    """
    combinedFragment = ET.SubElement(sequenceDiagram, 'combinedFragment')

    operator = ET.SubElement(combinedFragment, 'operator')
    operator.text = fragment.attrib.get('interactionOperator', '')

    lifelines = ET.SubElement(combinedFragment, 'lifelines')
    lifelines_covered = fragment.attrib.get('covered').split()

    # user will choose a lifeline from this list for the operands
    lifeline_choices = list()

    # Find lifelines that are covered by this combined fragment
    for lifeline_id in lifelines_covered:
        name = find_lifeline_by_id(oldroot, lifeline_id).attrib['name']
        ET.SubElement(lifelines, 'lifeline').text = name
        lifeline_choices.append(name)

    # add operand list to new XML
    operandList = ET.SubElement(combinedFragment, 'operandList')
    for op in fragment.findall('operand'):
        operand = ET.SubElement(operandList, 'operand')

        condition = ET.SubElement(operand, 'condition')
        condition_name = ET.SubElement(condition, 'name')
        condition_name.text = op.find('guard')\
            .find('specification').attrib['value']

        # user must choose which lifeline this operand belongs to
        belongs_to_lifeline_name = choose_lifeline(lifeline_choices,
                                                   condition_name.text)

        belongs_to_lifeline = ET.SubElement(condition, 'lifeline')
        belongs_to_lifeline.text = belongs_to_lifeline_name

        # Add messageEvents and each message number in this operand to new XML
        message_numbers = get_message_numbers_by_operand(oldroot, op)

        # ... but only if there is at least 1 message. this prevents the empty
        # <messageEvents /> tag from being added to intermediate.xml
        if len(message_numbers) > 0:
            messageEvents = ET.SubElement(operand, 'messageEvents')
            for msg_num in message_numbers:
                number = ET.SubElement(messageEvents, 'number')
                number.text = msg_num

        # find nested fragments. recursively call this function to add each
        # nested combined fragment to the intermediate.xml tree
        for additional_fragment in op.findall('fragment'):
            if additional_fragment.attrib[namespace + 'type']\
               == 'uml:CombinedFragment':
                handle_combined_fragment(additional_fragment, oldroot, operand)


def main(input_file, output_file):

    # open papyrus XML file
    tree = ET.parse(input_file)
    root = tree.getroot()

    # create initial structure for Hui's XML
    sequenceDiagramSpecification = ET.Element("sequenceDiagramSpecification")
    sequenceDiagram = ET.SubElement(sequenceDiagramSpecification,
                                    "sequenceDiagram")
    name = ET.SubElement(sequenceDiagram, 'name')
    name.text = root.attrib.get('name')

    oldroot = root.getchildren()[0]

    # check each tag and add it to new XML tree
    for lifeline in oldroot.findall('lifeline'):
        handle_lifeline(lifeline, sequenceDiagram)
    for message in oldroot.findall('message'):
        handle_message(message, oldroot, sequenceDiagram)
    for fragment in oldroot.findall('fragment'):
        if fragment.attrib[namespace + 'type'] == 'uml:CombinedFragment':
            handle_combined_fragment(fragment, oldroot, sequenceDiagram)

    # pretty print XML - write to file
    txt = ET.tostring(sequenceDiagramSpecification)
    with open(output_file, 'w') as fp:
        uglyXml = xml.dom.minidom.parseString(txt).toprettyxml(indent='  ')

        # do some cleanup
        text_re = re.compile('>\n\s+([^<>\s].*?)\n\s+</', re.DOTALL)
        prettyXML = text_re.sub('>\g<1></', uglyXml)
        fp.write(prettyXML)

if __name__ == "__main__":
    # parse the command line arguments
    parser = argparse.ArgumentParser(description='Papyrus XML converter')
    parser.add_argument('-i', '--input',
                        help='input file name', required=True)
    parser.add_argument('-o', '--output',
                        help='output file name', required=True)
    args = vars(parser.parse_args())

    # pass input and output file names to main
    main(args['input'], args['output'])
