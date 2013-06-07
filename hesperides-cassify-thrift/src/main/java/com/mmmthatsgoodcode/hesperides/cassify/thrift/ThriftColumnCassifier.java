package com.mmmthatsgoodcode.hesperides.cassify.thrift;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.CharSet;

import com.mmmthatsgoodcode.hesperides.core.Hesperides;
import com.mmmthatsgoodcode.hesperides.core.TransformationException;
import com.mmmthatsgoodcode.hesperides.cassify.AbstractConfigurableCassifier;
import com.mmmthatsgoodcode.hesperides.cassify.Cassifier;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesColumn.AbstractType;
import com.mmmthatsgoodcode.hesperides.cassify.model.HesperidesRow;

public class ThriftColumnCassifier extends AbstractConfigurableCassifier implements Cassifier<Column> {
	
	@Override
	public HesperidesRow cassify(Entry<String, List<Column>> object)
			throws TransformationException {
		
		LOG.debug("Processing {} columms for row {}", object.getValue().size(), object.getKey());

		HesperidesRow hesperidesRow = new HesperidesRow(object.getKey());
		for(Column column:object.getValue()) {
			
			HesperidesColumn hesperidesColumn = new HesperidesColumn();
			
			ByteBuffer nameBytes = (ByteBuffer) column.name;
			// split byte array with end-of-component byte ( 0 )
			
			/* Column name components ( see comment on composition below )
			 * ------------------------------------------------------------ */
			
			int componentNumber = 1; // which component we are processing
			while(nameBytes.remaining() > 4) { // if there is at least a header-sized chunk of bytes left
				
				LOG.trace("Processing component #"+componentNumber);
				
				nameBytes.position(nameBytes.position()+1); // dont care about the first byte of the header
				char alias = (char) nameBytes.get();
				short length = nameBytes.getShort();
				ByteBuffer componentValue = ByteBuffer.allocate(length);
				nameBytes.get(componentValue.array());
				nameBytes.position(nameBytes.position()+1); // dont care about the end-of-component byte
			
				switch(HINT_TO_CASSANDRA_TYPE.get( getCassandraTypeAliases().inverse().get(alias) )) {
					case Hesperides.Hints.STRING:
						hesperidesColumn.addNameComponent(new String(componentValue.array(), Charset.forName("UTF-8")));
					break;
					case Hesperides.Hints.FLOAT:
						hesperidesColumn.addNameComponent(componentValue.asFloatBuffer().get());
					break;
					case Hesperides.Hints.LONG:
						hesperidesColumn.addNameComponent(componentValue.asLongBuffer().get());
					break;
					case Hesperides.Hints.DATE:
						hesperidesColumn.addNameComponent(new Date(componentValue.asLongBuffer().get()));
					break;
					case Hesperides.Hints.NULL:
						hesperidesColumn.addNullNameComponent();
					break;
					case Hesperides.Hints.BOOLEAN:
						hesperidesColumn.addNameComponent(componentValue.get() == (byte)1?true:false);
					break;
					case Hesperides.Hints.INT:
						hesperidesColumn.addNameComponent(componentValue.asIntBuffer().get());
					break;
					default:
						throw new TransformationException("Could not map alias "+alias+" to a Hesperides.Hint, check configuration");
				
				}
					
				LOG.trace("Processed component #"+componentNumber+", "+nameBytes.remaining()+"bytes remain");
				
				componentNumber++;

			}
			
			LOG.debug("Processed {} total components in column name", hesperidesColumn.getNameComponents().size());
			
			
			/* Column value
			 * ------------- */
			
			// last component should be the type hint
			int valueHint = ((HesperidesColumn.IntegerValue) hesperidesColumn.getNameComponents().get(hesperidesColumn.getNameComponents().size()-1)).getValue();
			LOG.debug("Processing value of hint {}", valueHint);
			
			switch(valueHint) {
			
			case Hesperides.Hints.STRING:
				hesperidesColumn.setValue(new String(column.value.array(), Charset.forName("UTF-8")));
			break;
			case Hesperides.Hints.FLOAT:
				hesperidesColumn.setValue(column.value.asFloatBuffer().get());
			break;
			case Hesperides.Hints.LONG:
				hesperidesColumn.setValue(column.value.asLongBuffer().get());
			break;
			case Hesperides.Hints.DATE:
				hesperidesColumn.setValue(new Date(column.value.asLongBuffer().get()));
			break;
			case Hesperides.Hints.NULL:

				break;
			case Hesperides.Hints.BOOLEAN:
				hesperidesColumn.setValue(column.value.get() == (byte)1?true:false);
			break;
			case Hesperides.Hints.INT:
				hesperidesColumn.setValue(column.value.asIntBuffer().get());
			break;
			case Hesperides.Hints.BYTES:
				hesperidesColumn.setValue(column.value.array());
			break;
			default:
				throw new TransformationException("Could not deserialize value hint of "+valueHint);
				
			}
			
			hesperidesRow.addColumn(hesperidesColumn);
		
		}
		
		return hesperidesRow;
		
	}
	
	@Override
	public Entry<String, List<Column>> cassify(HesperidesRow row)
			throws TransformationException {

		/* Row key
		 * -------- */

		SimpleEntry<String, List<Column>> result = new SimpleEntry<String, List<Column>>(row.getKey(), new ArrayList<Column>());

		for(HesperidesColumn hesperidesColumn:row.getColumns()) {
			// create Column
			Column column = new Column();
			
			/* Column name components
			 * -----------------------
			 * For each component, the following byte array should be prepared:
			 * - 2 bytes header, in which the first bit of the first byte is 1, the second byte is the alias char's byte value
			 * - 2 byte unsigned short is the size of the component
			 * - the actual component in bytes
			 * - end of component byte: 0
			 * A total max size of 64KB must be enforced on column name */
			
			hesperidesColumn.addNameComponent(hesperidesColumn.getValue().getHint()); // value hint component
			
			ByteArrayOutputStream columnNameBytes = new ByteArrayOutputStream();
			for(AbstractType component:hesperidesColumn.getNameComponents()) {
				
				ByteBuffer prefix = ByteBuffer.wrap(new byte[4]);
				byte aliasFlag = 0;	aliasFlag = (byte) (aliasFlag | (1 << 0));
				Character aliasChar = getCassandraTypeAliases().get( HINT_TO_CASSANDRA_TYPE.inverse().get( component.getHint() ) );
				if (aliasChar == null) throw new TransformationException("Could not get alias for type "+Hesperides.Hints.typeToHint(component.getValue().getClass()));
				byte aliasByte = (byte) aliasChar.charValue();
				
				byte[] componentValue = null;
				if (component instanceof HesperidesColumn.StringValue) componentValue = ((String)component.getValue()).getBytes();
				else if (component instanceof HesperidesColumn.BooleanValue) componentValue = new byte[]{(byte) ((Boolean)component.getValue()?1:0)};
				else if (component instanceof HesperidesColumn.LongValue) componentValue = ByteBufferUtil.bytes((long)component.getValue()).array();
				else if (component instanceof HesperidesColumn.FloatValue) componentValue = ByteBufferUtil.bytes((float)component.getValue()).array();
				else if (component instanceof HesperidesColumn.IntegerValue) componentValue = ByteBufferUtil.bytes((int)component.getValue()).array();
				else if (component instanceof HesperidesColumn.DateValue) componentValue = ByteBufferUtil.bytes(((Date)component.getValue()).getTime()).array();
				else if (component instanceof HesperidesColumn.NullValue) componentValue = new byte[]{(byte) 0};
				else throw new TransformationException("Could not serialize component of type "+component.getClass().getSimpleName());
				
				prefix.put(aliasFlag);
				prefix.put(aliasByte);
				prefix.putShort((short) componentValue.length); // 2byte short
				
				if ((columnNameBytes.size() + componentValue.length+5) > 64*1024) throw new TransformationException("Column name over 64KB!");
				
				try {
					columnNameBytes.write(prefix.array());
					columnNameBytes.write(componentValue);
					columnNameBytes.write(new byte[]{(byte)0});
				} catch (IOException e) {
					
				}
				
			}
			
			column.name = ByteBuffer.wrap(columnNameBytes.toByteArray());
			
			/* Column value
			 * -------------
			 * Use cassandra-clientutils to encode values */
	
			
			switch(hesperidesColumn.getValue().getHint()) {
	
				case Hesperides.Hints.STRING:
					column.value = ByteBufferUtil.bytes( ((HesperidesColumn.StringValue) hesperidesColumn.getValue() ).getValue());
				break;
				case Hesperides.Hints.FLOAT:
					column.value = ByteBufferUtil.bytes( ((HesperidesColumn.FloatValue) hesperidesColumn.getValue() ).getValue());
				break;
				case Hesperides.Hints.LONG:
					column.value = ByteBufferUtil.bytes( ((HesperidesColumn.LongValue) hesperidesColumn.getValue() ).getValue());
				break;
				case Hesperides.Hints.DATE:
					column.value = ByteBufferUtil.bytes( ((HesperidesColumn.DateValue) hesperidesColumn.getValue() ).getValue().getTime() );
				break;
				case Hesperides.Hints.NULL:
					column.value = ByteBuffer.wrap(new byte[]{(byte)0});
				break;
				case Hesperides.Hints.BOOLEAN:
					column.value = ByteBuffer.wrap(new byte[]{(byte)(((HesperidesColumn.BooleanValue) hesperidesColumn.getValue() ).getValue()?1:0)});
				break;
				case Hesperides.Hints.INT:
					column.value = ByteBufferUtil.bytes( ((HesperidesColumn.IntegerValue) hesperidesColumn.getValue() ).getValue());					
				break;
				case Hesperides.Hints.BYTES:
					column.value = ByteBuffer.wrap(((HesperidesColumn.ByteValue) hesperidesColumn.getValue() ).getValue());				
				break;
				default:
					throw new TransformationException("Could not serialize column value of type(hint) "+hesperidesColumn.getValue().getHint());			
				
			}
			
			result.getValue().add(column);
		
		}
		
		return result;
	}


}
