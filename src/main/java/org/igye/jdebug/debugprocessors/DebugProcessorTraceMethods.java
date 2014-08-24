package org.igye.jdebug.debugprocessors;

import org.igye.jdebug.DebugProcessor;
import org.igye.jdebug.MessageReader;
import org.igye.jdebug.MessageWriter;
import org.igye.jdebug.messages.EventModifier;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.EventKind;
import org.igye.jdebug.messages.constants.SuspendPolicy;
import org.igye.jdebug.messages.core.IdSizes;
import org.igye.jdebug.messages.core.ReplyPacket;
import org.igye.jdebug.messages.eventmodifiers.ClassMatch;
import org.igye.jdebug.messages.impl.IdSizesCommand;
import org.igye.jdebug.messages.impl.IdSizesReply;
import org.igye.jdebug.messages.impl.SetCommand;
import org.igye.jdebug.messages.impl.SetReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugProcessorTraceMethods implements DebugProcessor {
    private static Logger log = LoggerFactory.getLogger(DebugProcessorTraceMethods.class);

    private MessageReader msgReader;
    private MessageWriter msgWriter;

    @Override
    public void run() {
        try {
            initIdSizes();

            long id = msgWriter.putMessage(new SetCommand(
                    EventKind.METHOD_ENTRY,
                    SuspendPolicy.NONE,
                    null
            ));
            SetReply setReply = new SetReply(getReplyById(id));
            int requestId = setReply.getRequestId();
            System.out.println("setReply.getErrorCode() = " + setReply.getErrorCode());
            System.out.println("setReply.getRequestId() = " + setReply.getRequestId());


        } catch (InterruptedException e) {
            log.error("InterruptedException in run().", e);
        }
    }

    private void initIdSizes() throws InterruptedException {
        long id = msgWriter.putMessage(new IdSizesCommand());
        IdSizesReply idSizesReply = new IdSizesReply(getReplyById(id));
        IdSizes.setFieldIDSize(idSizesReply.getFieldIDSize());
        IdSizes.setFrameIDSize(idSizesReply.getFrameIDSize());
        IdSizes.setMethodIDSize(idSizesReply.getMethodIDSize());
        IdSizes.setObjectIDSize(idSizesReply.getObjectIDSize());
        IdSizes.setReferenceTypeIDSize(idSizesReply.getReferenceTypeIDSize());

        System.out.println("IdSizes.getFieldIDSize() = " + IdSizes.getFieldIDSize());
        System.out.println("IdSizes.getFrameIDSize() = " + IdSizes.getFrameIDSize());
        System.out.println("IdSizes.getMethodIDSize() = " + IdSizes.getMethodIDSize());
        System.out.println("IdSizes.getObjectIDSize() = " + IdSizes.getObjectIDSize());
        System.out.println("IdSizes.getReferenceTypeIDSize() = " + IdSizes.getReferenceTypeIDSize());
    }

    private ReplyPacket getReplyById(long id) throws InterruptedException {
        JdwpMessage msg = null;
        while (true) {
            msg = msgReader.takeMessage();
            if (msg.getFlags() == JdwpMessage.REPLY_FLAG && id == msg.getId()) {
                return (ReplyPacket) msg;
            }
        }
    }

    @Override
    public void setWriterAndReader(MessageWriter messageWriter, MessageReader messageReader) {
        this.msgWriter = messageWriter;
        this.msgReader = messageReader;
    }
}
