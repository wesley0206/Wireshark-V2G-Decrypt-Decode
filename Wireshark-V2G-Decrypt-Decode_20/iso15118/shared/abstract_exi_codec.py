import json
import logging
from builtins import Exception

from iso15118.shared.iexi_codec import IEXICodec
from iso15118.shared.settings import JAR_FILE_PATH

logger = logging.getLogger(__name__)


class AbsExificientEXICodec(IEXICodec):
    def __init__(self):
        from py4j.java_gateway import JavaGateway

        logging.getLogger("py4j").setLevel(logging.CRITICAL)
        self.gateway = JavaGateway.launch_gateway(
            classpath=JAR_FILE_PATH,
            die_on_exit=True,
            javaopts=["--add-opens", "java.base/java.lang=ALL-UNNAMED"],
        )
        self.abs_exi_codec = self.gateway.jvm.com.siemens.ct.exi.main.cmd.AbstractEXICodec()
        
    def unmarshallToMessage(self, stream: str) :
        decoded_message = self.abs_exi_codec.unmarshallToMessage(stream)
        return decoded_message