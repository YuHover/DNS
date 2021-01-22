package cn.edu.bupt.message;

import cn.edu.bupt.Global;
import cn.edu.bupt.message.question.QCLASS;
import cn.edu.bupt.message.question.QTYPE;
import cn.edu.bupt.message.question.Question;
import cn.edu.bupt.utils.NumBytesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DNSRequest {
    private static final int QOATR_POSITION = 2;
    private static final int RZR_POSITION = 3;
    private static final int QDCOUNT_POSITION = 4;
    private static final int ANCOUNT_POSITION = 6;
    private static final int QUESTION_POSITION = 12;

    private final int POINTER_POSITION;
    private final int ANSWER_QTYPE_POSITION;
    private final int ANSWER_QCLASS_POSITION;
    private final int TTL_POSITION;
    private final int RDLENGTH_POSITION;
    private final int RDATA_POSITION;

    private static final String NOT_DNS_REQUEST = "Not DNS request data.";

    private final byte[] data;
    private int length;

    private final OPCODE opcode;
    private final boolean truncated;
    private final int questionCount;
    private final List<Question> questions;

    public DNSRequest(byte[] data, int length) {
        try {
            this.data = Arrays.copyOf(data, Global.BUFFER_LENGTH);
            this.length = length;

            if ((this.data[QOATR_POSITION] & 0x80) != 0) {
                throw new Exception(NOT_DNS_REQUEST);
            }

            this.POINTER_POSITION = this.length;
            this.ANSWER_QTYPE_POSITION = this.POINTER_POSITION + 2;
            this.ANSWER_QCLASS_POSITION = this.ANSWER_QTYPE_POSITION + 2;
            this.TTL_POSITION = this.ANSWER_QCLASS_POSITION + 2;
            this.RDLENGTH_POSITION = this.TTL_POSITION + 4;
            this.RDATA_POSITION = this.RDLENGTH_POSITION + 2;

            this.opcode = OPCODE.ofValue(obtainOpcodeValue());
            this.truncated = ((this.data[QOATR_POSITION] & 0x02) != 0);

            this.questionCount =
                    NumBytesUtil.getShortFromTwoBytes(this.data[QDCOUNT_POSITION], this.data[QDCOUNT_POSITION + 1]);
            this.questions = obtainQuestions();
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getData() {
        return this.data;
    }

    public int getLength() {
        return this.length;
    }

    public OPCODE getOpcode() {
        return this.opcode;
    }

    public boolean isTruncated() {
        return this.truncated;
    }

    public int getQuestionCount() {
        return this.questionCount;
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public void flipToResponse() {
        this.data[QOATR_POSITION] = (byte) (this.data[QOATR_POSITION] | 0x80 & 0xf9);
    }

    public void setLegality(boolean legal) {
        if (legal) {
            data[RZR_POSITION] = (byte) 0x80;
        }
        else {
            data[RZR_POSITION] = (byte) 0x85;
        }
    }

    public void setAnswerCount(short count) {
        byte[] shortBytes = NumBytesUtil.getBytesFromShort(count);
        setTemplate(ANCOUNT_POSITION, shortBytes);
    }

    public void setPointer(short pointer) {
        byte[] shortBytes = NumBytesUtil.getBytesFromShort(pointer);
        setTemplate(this.POINTER_POSITION, shortBytes);
    }

    public void setAnswerQTYPE(QTYPE qType) {
        short value = (short) qType.getValue();
        byte[] shortBytes = NumBytesUtil.getBytesFromShort(value);
        setTemplate(this.ANSWER_QTYPE_POSITION, shortBytes);
    }

    public void setAnswerQCLASS(QCLASS qClass) {
        short value = (short) qClass.getValue();
        byte[] shortBytes = NumBytesUtil.getBytesFromShort(value);
        setTemplate(this.ANSWER_QCLASS_POSITION, shortBytes);
    }

    public void setTTL(Integer ttl) {
        byte[] intBytes = NumBytesUtil.getBytesFromInt(ttl);
        setTemplate(this.TTL_POSITION, intBytes);
    }

    public void setRDLength(short length) {
        byte[] shortBytes = NumBytesUtil.getBytesFromShort(length);
        setTemplate(this.RDLENGTH_POSITION, shortBytes);
    }

    public void setRDATA(byte[] rdata) {
        setTemplate(this.RDATA_POSITION, rdata);
    }

    private void setTemplate(int position, byte[] bytes) {
        for (byte b : bytes) {
            this.data[position] = b;
            position++;
        }

        if (this.length < position) {
            this.length = position;
        }
    }

    private List<Question> obtainQuestions() {
        List<Question> questions = new ArrayList<>();
        int begin = QUESTION_POSITION;
        int end = begin;
        for (int i = 0; i < this.questionCount; i++) {
            while (end <= this.length && this.data[end] != 0x00) end++;
            end += Question.QTYPE_LENGTH + Question.QCLASS_LENGTH;
            questions.add(new Question(Arrays.copyOfRange(data, begin, end + 1)));

            end++;
            begin = end;
        }
        return questions;
    }

    private int obtainOpcodeValue() {
        return (this.data[QOATR_POSITION] & 0x78) >> 3;
    }
}
